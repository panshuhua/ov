package com.ivay.ivay_app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import com.ivay.ivay_app.dto.CUSTOMER_INFO;
import com.ivay.ivay_app.dto.LIST_TRANS;
import com.ivay.ivay_app.dto.VTPResponseStatus;
import com.ivay.ivay_app.dto.VTPTranInfo;
import com.ivay.ivay_app.dto.VTPTranOutputData;
import com.ivay.ivay_app.dto.VTPTranProcessInput;
import com.ivay.ivay_app.dto.VTPTranProcessOutput;
import com.ivay.ivay_app.dto.VTPUpdateTranStatusResponse;
import com.ivay.ivay_app.service.VTPRepayApiService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.HttpClientUtils;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.RSAEncryptShaVTP;
import com.ivay.ivay_common.utils.UUIDUtils;
import com.ivay.ivay_repository.dao.master.VTPTransationDao;
import com.ivay.ivay_repository.dto.VTPTranProcessInfo;
import com.ivay.ivay_repository.dto.XVtpRepayTranInfo;

@Service
public class VTPRepayApiServiceImpl implements VTPRepayApiService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(VTPRepayApiServiceImpl.class);

    @Resource
    VTPTransationDao vTPTransationDao;

    @Value("${VTPpartnerCode}")
    private String VTPpartnerCode;
    @Value("${VTPApiUrl}")
    private String VTPApiUrl;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public VTPTranInfo getTransInfo(String id, String xToken, String xSigned) {
        logger.info("获取客户信息方法..........................");
        // x-token校验
        if (!VTPpartnerCode.equals(xToken)) {
            return null;
        }

        // id校验
        logger.info("加密前查询id：{}", id);
        String md5Id = DigestUtils.md5DigestAsHex(id.getBytes());
        logger.info("md5加密后的：{}", md5Id);
        boolean b = RSAEncryptShaVTP.decrypt2Sha1(md5Id, xSigned);
        logger.info("解密验证：{}", b);

        if (!b) {
            return null;
        }

        // 校验成功，返回客户信息
        // id:订单id-客户信息
        // 根据orderId查询用户信息
        XVtpRepayTranInfo vtpTranInfo = vTPTransationDao.queryRepayTranInfo(id);
        logger.info("根据订单号查询到的用户信息：{}", vtpTranInfo.toString());

        VTPTranInfo tranInfo = new VTPTranInfo();
        tranInfo.setQUERY_CODE(id);
        CUSTOMER_INFO customerInfo = new CUSTOMER_INFO();
        customerInfo.setCODE(id);
        customerInfo.setPHONE(vtpTranInfo.getPhone());
        customerInfo.setDISPLAYNAME(vtpTranInfo.getName());
        String extendInfos = "identityCard||" + vtpTranInfo.getIdentityCard() + "||birthday||"
            + DateUtils.dateToString_DD_MM_YYYY(vtpTranInfo.getBirthday()) + "||place||" + vtpTranInfo.getPlace();
        logger.info("用户扩展信息：{}", extendInfos);
        customerInfo.setEXTENDS_INFO(extendInfos);
        tranInfo.setCUSTOMER_INFO(customerInfo);
        List<LIST_TRANS> transList = new ArrayList<LIST_TRANS>();
        String tranId = UUIDUtils.getUUID().replace("-", "");
        Integer type = 2;
        // 使用订单关联时，一个客户下就只会有一笔订单
        LIST_TRANS listTran = new LIST_TRANS();
        listTran.setTRANS_ID(tranId);
        listTran.setTYPE(type);
        listTran.setAMOUNT(vtpTranInfo.getShouldRepayAmount());
        transList.add(listTran);
        tranInfo.setLIST_TRANS(transList);
        logger.info("查询用户信息接口返回：{}", tranInfo);
        return tranInfo;
    }

    @Override
    public VTPTranProcessOutput transactionProcess(VTPTranProcessInput input, String xToken, String xSigned) {
        logger.info("交易进程方法..........................");
        logger.info("所有的参数，{}", input);
        // 返回给VTP的信息
        VTPTranProcessOutput output = new VTPTranProcessOutput();
        output.setSIGNED(xSigned);
        VTPTranOutputData data = new VTPTranOutputData();
        if (input == null) {
            data.setTRANS_STATUS(VTPResponseStatus.LackOfData.getCode());
            data.setTRANS_MSG(VTPResponseStatus.LackOfData.getMessage());
            output.setDATA(data);
            return output;
        }

        data.setREF_ID(input.getREF_ID());
        data.setTRANS_ID(input.getTRANS_ID());
        data.setTRANS_DATE(input.getTRANS_DATE());
        // x-token校验
        if (!VTPpartnerCode.equals(xToken)) {
            data.setTRANS_STATUS(VTPResponseStatus.InvalidPartnerCode.getCode());
            data.setTRANS_MSG(VTPResponseStatus.InvalidPartnerCode.getMessage());
            output.setDATA(data);
            return output;
        }

        // x-token校验
        String encryptStr = "TRANS_ID||" + input.getTRANS_ID() + "||REF_ID||" + input.getREF_ID() + "||CUSTOMER_CODE||"
            + input.getCUSTOMER_CODE() + "||AMOUNT||" + input.getAMOUNT() + "||TRANS_DATE||" + input.getTRANS_DATE();
        logger.info("加密前参数字符串：{}", encryptStr);
        // id校验
        String md5Str = DigestUtils.md5DigestAsHex(encryptStr.getBytes());
        logger.info("md5加密后：{}", md5Str);
        boolean b = RSAEncryptShaVTP.decrypt2Sha1(md5Str, xSigned);

        logger.info("解密验证：{}", b);
        if (!b) {
            data.setTRANS_STATUS(VTPResponseStatus.InvalidSignature.getCode());
            data.setTRANS_MSG(VTPResponseStatus.InvalidSignature.getMessage());
            output.setDATA(data);
            return output;
        }

        // 根据交易id查询该交易是否存在了
        VTPTranProcessInfo vtpTranProcessInfo = vTPTransationDao.queryTransStatus(input.getTRANS_ID());
        if (vtpTranProcessInfo != null) {
            data.setTRANS_STATUS(VTPResponseStatus.OverlappingTransaction.getCode());
            data.setTRANS_MSG(VTPResponseStatus.OverlappingTransaction.getMessage());
            output.setDATA(data);
            return output;
        }

        VTPTranProcessInfo info = new VTPTranProcessInfo();
        info.setTransId(input.getTRANS_ID());
        info.setRefId(input.getREF_ID());
        info.setCustomerCode(input.getCUSTOMER_CODE());
        info.setAmount(input.getAMOUNT());
        info.setFee(input.getFEE());
        info.setContent(input.getCONTENT());
        info.setAgentUser(input.getAGENT_USER());
        info.setAgentName(input.getAGENT_NAME());
        info.setTransDate(input.getTRANS_DATE());
        info.setTransStatus(VTPResponseStatus.Successful.getCode());
        info.setTransMsg(VTPResponseStatus.Successful.getMessage());

        // VTP发过来的交易信息存入数据库
        vTPTransationDao.saveTranProcessInfo(info);
        // 返回给VTP的信息
        data.setTRANS_STATUS(VTPResponseStatus.Successful.getCode()); // 交易状态为0表示交易成功
        data.setTRANS_MSG(VTPResponseStatus.Successful.getMessage()); // 交易成功
        output.setDATA(data);

        return output;

    }

    @Override
    public VTPTranProcessOutput trackingStatus(String tid, String xToken, String xSigned) {
        logger.info("查询交易状态方法..........................");
        // 返回给VTP的信息
        VTPTranProcessOutput output = new VTPTranProcessOutput();
        output.setSIGNED(xSigned);
        VTPTranOutputData data = new VTPTranOutputData();
        // x-token校验
        if (!VTPpartnerCode.equals(xToken)) {
            data.setTRANS_STATUS(VTPResponseStatus.InvalidPartnerCode.getCode());
            data.setTRANS_MSG(VTPResponseStatus.InvalidPartnerCode.getMessage());
            output.setDATA(data);
            return output;
        }

        // tid（交易id)校验
        logger.info("加密前tid-交易id：{}", tid);
        String md5tid = DigestUtils.md5DigestAsHex(tid.getBytes());
        logger.info("md5加密后：{}", md5tid);
        boolean b = RSAEncryptShaVTP.decrypt2Sha1(md5tid, xSigned);

        logger.info("解密验证结果：{}", b);
        if (!b) {
            data.setTRANS_STATUS(VTPResponseStatus.InvalidSignature.getCode());
            data.setTRANS_MSG(VTPResponseStatus.InvalidSignature.getMessage());
            output.setDATA(data);
            return output;
        }

        // 根据交易id查询该交易的状态
        VTPTranProcessInfo vtpTranProcessInfo = vTPTransationDao.queryTransStatus(tid);
        logger.info("查询交易id是否存在：{}", vtpTranProcessInfo);
        if (vtpTranProcessInfo == null) {
            data.setTRANS_STATUS(VTPResponseStatus.InvalidTransaction.getCode());
            data.setTRANS_MSG(VTPResponseStatus.InvalidTransaction.getMessage());
            output.setDATA(data);
            return output;
        }

        VTPTranProcessOutput vtpTranProcessOutput = new VTPTranProcessOutput();
        vtpTranProcessOutput.setSIGNED(xSigned);
        data.setTRANS_ID(vtpTranProcessInfo.getTransId());
        data.setREF_ID(vtpTranProcessInfo.getRefId());
        data.setTRANS_DATE(vtpTranProcessInfo.getTransDate());
        data.setTRANS_STATUS(vtpTranProcessInfo.getTransStatus());
        data.setTRANS_MSG(vtpTranProcessInfo.getTransMsg());
        vtpTranProcessOutput.setDATA(data);
        logger.info("查询交易状态的返回值：{}", vtpTranProcessOutput);
        return vtpTranProcessOutput;
    }

    @Override
    public VTPUpdateTranStatusResponse updateTransactionStatus(VTPTranOutputData data) throws IOException {
        VTPTranProcessOutput output = new VTPTranProcessOutput();
        String transId = data.getTRANS_ID();
        String refId = data.getREF_ID();
        Long transDate = data.getTRANS_DATE();
        String transMsg = data.getTRANS_MSG();
        // 加密前的字符串：TRANS_ID||VTP123||REF_ID||Uk1||TRANS_DATE||1561947953||TRANS_MSG||OK
        String signedStr =
            "TRANS_ID||" + transId + "||REF_ID||" + refId + "||TRANS_DATE||" + transDate + "||TRANS_MSG||" + transMsg;
        logger.info("加密前：{}", signedStr);
        // md5加密
        signedStr = DigestUtils.md5DigestAsHex(signedStr.getBytes());
        // RSA加密
        String signed = RSAEncryptShaVTP.encrypt2Sha1(signedStr);
        logger.info("加密后：{}", signed);
        output.setSIGNED(signed);
        output.setDATA(data);
        // 调用VTP的接口
        String json = HttpClientUtils.postByJsonVTP(VTPApiUrl, output, VTPpartnerCode);
        logger.info("调用接口返回的字符串：{}", json);
        VTPUpdateTranStatusResponse vtpResponse = JsonUtils.jsonToPojo(json, VTPUpdateTranStatusResponse.class);
        return vtpResponse;
    }

}
