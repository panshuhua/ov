package com.ivay.ivay_app.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ivay.ivay_app.service.XAccountCheckService;
import com.ivay.ivay_common.utils.ExcelUtil;
import com.ivay.ivay_common.utils.FileUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.CheckAccountDataDao;
import com.ivay.ivay_repository.model.AccountCheckResult;
import com.ivay.ivay_repository.model.BaokimCollectionData;
import com.ivay.ivay_repository.model.BaokimTransferData;

@Service
public class XAccountCheckServiceImpl implements XAccountCheckService {

    private static final Logger logger = LoggerFactory.getLogger(XAccountCheckServiceImpl.class);

    @Resource
    CheckAccountDataDao checkAccountDataDao;

    @Value("${files.path}")
    private String filesPath;

    @Override
    public void importAccountDatas(String type, MultipartFile file) throws IOException, ParseException {
        // 上传文件
        // 以md5作为文件名
        String filename = file.getOriginalFilename();
        String md5 = FileUtil.fileMd5(file.getInputStream());
        filename = filename.substring(filename.lastIndexOf(".")); // 后缀名
        String pathname = FileUtil.getPath() + md5 + filename;
        String fullPath = filesPath + pathname;
        String dirPath = fullPath.substring(0, fullPath.lastIndexOf("/"));
        System.out.println(dirPath);

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (dir.exists()) {
            String[] files = dir.list();
            boolean isExist = false;
            for (String s : files) {
                String name = md5 + filename;
                if (s.equals(name)) {
                    logger.info("文件已存在，无需重复上传");
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                // 保存文件
                FileUtil.saveFile(file, fullPath);

                // 导入数据：1-借款；2-还款
                if (SysVariable.CHECK_TYPE_REPAY.equals(type)) {
                    importBkCollectionTranData(fullPath);
                } else if (SysVariable.CHECK_TYPE_LOAN.equals(type)) {
                    importBkTransferDatas(fullPath);
                }

            }
        }

    }

    /**
     * 导入baokim还款交易数据到数据库
     * 
     * @param filePath
     * @throws IOException
     */
    private void importBkCollectionTranData(String filePath) throws IOException {
        // 读取上传的excel文档
        List<XSSFRow> rowlist = ExcelUtil.readXls(filePath);
        logger.info("导入的总行数：" + rowlist.size());
        List<BaokimCollectionData> bkTranList = new ArrayList<BaokimCollectionData>();
        for (int i = 0; i < rowlist.size(); i++) {
            if (i >= 1) {
                XSSFCell transactionIdBaokim = rowlist.get(i).getCell(0);
                XSSFCell timeRecorded = rowlist.get(i).getCell(1);
                XSSFCell accountNo = rowlist.get(i).getCell(2);
                XSSFCell amount = rowlist.get(i).getCell(3);
                XSSFCell accountName = rowlist.get(i).getCell(7);
                XSSFCell orderId = rowlist.get(i).getCell(10);
                XSSFCell accountType = rowlist.get(i).getCell(11);
                XSSFCell status = rowlist.get(i).getCell(12);

                BaokimCollectionData data = new BaokimCollectionData();
                // 判断是否是空行，空行不保存到数据库
                if (StringUtils.isEmpty(ExcelUtil.getValue(transactionIdBaokim))) {
                    continue;
                }
                data.setTransactionIdBaokim(ExcelUtil.getValue(transactionIdBaokim));
                data.setTimeRecorded(ExcelUtil.getValue(timeRecorded));
                data.setAccountNo(ExcelUtil.getValue(accountNo));
                data.setAmount(ExcelUtil.getValue(amount).replace(",", ""));
                data.setAccountName(ExcelUtil.getValue(accountName));
                data.setOrderId(ExcelUtil.getValue(orderId));
                data.setAccountType(ExcelUtil.getValue(accountType));
                data.setStatus(ExcelUtil.getValue(status));

                bkTranList.add(data);
            }

        }

        checkAccountDataDao.insertBatchBkCollTrans(bkTranList);
    }

    @Override
    public void checkBkCollectionDatas(String time) {
        AccountCheckResult result = new AccountCheckResult();
        AccountCheckResult baokimResult = checkAccountDataDao.countByBkCollection(time);
        AccountCheckResult ovayResult = checkAccountDataDao.countByOvayCollection(time);
        result.setBaokimAmount(baokimResult.getBaokimAmount());
        result.setBaokimCount(baokimResult.getBaokimCount());
        result.setOvayAmount(ovayResult.getOvayAmount());
        result.setOvayCount(ovayResult.getOvayCount());
        result.setPartner(SysVariable.PARTNER_BAOKIM);
        result.setType(SysVariable.CHECK_TYPE_REPAY);
        result.setTime(time);
        // 比对结果存入数据库
        checkAccountDataDao.insertResult(result);
    }

    /**
     * 导入baokim的借款数据
     * 
     * @param filePath
     * @throws IOException
     * @throws ParseException
     */
    public void importBkTransferDatas(String filePath) throws IOException, ParseException {
        // 读取上传的excel文档
        List<XSSFRow> rowlist = ExcelUtil.readXls(filePath);
        logger.info("导入的总行数：" + rowlist.size());
        List<BaokimTransferData> bkTranList = new ArrayList<BaokimTransferData>();
        for (int i = 0; i < rowlist.size(); i++) {
            if (i >= 4) {
                XSSFCell transTime = rowlist.get(i).getCell(1);
                XSSFCell baokimTransId = rowlist.get(i).getCell(4);
                XSSFCell amount = rowlist.get(i).getCell(7);
                XSSFCell transferRealAmount = rowlist.get(i).getCell(9);
                XSSFCell customerName = rowlist.get(i).getCell(10);
                XSSFCell cardNo = rowlist.get(i).getCell(11);
                XSSFCell status = rowlist.get(i).getCell(12);

                BaokimTransferData data = new BaokimTransferData();
                // 判断是否是空行，空行不保存到数据库
                if (StringUtils.isEmpty(ExcelUtil.getValue(transTime))) {
                    continue;
                }
                // 02-07-2019 17:02格式转换为2019-07-02 17:02
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date time = sdf.parse(ExcelUtil.getValue(transTime));
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String tranTime = sdf.format(time);
                data.setTransTime(tranTime);
                data.setBaokimTransId(ExcelUtil.getValue(baokimTransId));
                data.setAmount(ExcelUtil.getValue(amount).replace(",", ""));
                data.setTransferRealAmount(ExcelUtil.getValue(transferRealAmount).replace(",", ""));
                data.setCustomerName(ExcelUtil.getValue(customerName));
                data.setCardNo(ExcelUtil.getValue(cardNo));
                data.setStatus(ExcelUtil.getValue(status));

                bkTranList.add(data);
            }

        }

        checkAccountDataDao.insertBatchBkTransfers(bkTranList);

    }

    @Override
    public void checkBkTransferDatas(String time) {
        AccountCheckResult result = new AccountCheckResult();
        AccountCheckResult baokimResult = checkAccountDataDao.countByBkTransfer(time);
        AccountCheckResult ovayResult = checkAccountDataDao.countByOvayTransfer(time);
        result.setBaokimAmount(baokimResult.getBaokimAmount());
        result.setBaokimCount(baokimResult.getBaokimCount());
        result.setOvayAmount(ovayResult.getOvayAmount());
        result.setOvayCount(ovayResult.getOvayCount());
        result.setPartner(SysVariable.PARTNER_BAOKIM);
        result.setType(SysVariable.CHECK_TYPE_LOAN);
        result.setTime(time);
        // 比对结果存入数据库
        checkAccountDataDao.insertResult(result);
    }

}
