package com.ivay.ivay_app.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.ValVirtualAccountReq;
import com.ivay.ivay_app.dto.ValVirtualAccountRsp;
import com.ivay.ivay_app.service.SysLogService;
import com.ivay.ivay_app.service.XVirtualAccountService;
import com.ivay.ivay_common.utils.HttpClientUtils;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.RSAEncryptShaCollection;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_common.utils.UUIDUtils;
import com.ivay.ivay_repository.dao.master.TokenDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dao.master.XVirtualAccountDao;
import com.ivay.ivay_repository.dto.XRecordLoanInfo;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XRecordRepayment;
import com.ivay.ivay_repository.model.XUserInfo;
import com.ivay.ivay_repository.model.XVirtualAccount;

@Service
public class XVirtualAccountServiceImpl implements XVirtualAccountService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    TokenDao tokenDao;
    @Autowired
    XVirtualAccountDao xVirtualAccountDao;
    @Autowired
    XUserInfoDao xUserInfoDao;
    @Autowired
    XRecordLoanDao xRecordLoanDao;
    @Autowired
    SysLogService sysLogService;

    @Value("${api_collection_url}")
    private String collectionUrl;

    @Value("${api_partner_code}")
    private String apiParterCode;

    @Override
    public String getRequestId(String PartnerCode, String date) {
        String uniqueId = tokenDao.getUniqueId();
        return PartnerCode + "BK" + date + uniqueId;
    }

    @Override
    public XVirtualAccount queryVirtualAccount(String id) {
        return xVirtualAccountDao.queryVirtualAccount(id);
    }

    @Override
    public ValVirtualAccountRsp addVirtualAccount(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment) {
        String requestId = UUIDUtils.getRequestId();
        String requestTime = UUIDUtils.getRequestTime();
        String partnerCode = apiParterCode;
        String operation = SysVariable.API_OPERATION_REGISTER_VIRTUALACCOUNT;
        String clientIdNo = MsgAuthCode.getAuthNineCode();
        String accountType = SysVariable.API_ACC_TYPE_SECOND; // 2-根据orderId

        // String orderId //订单id
        // 根据usergid查询用户信息
        XUserInfo xUserInfo = xUserInfoDao.getByGid(xRecordLoan.getUserGid()); // accountType=1时需要

        String accName = xUserInfo.getName();
        if (accName != null) {
            accName = accName.trim();
        }
        // Date birthday=xUserInfo.getBirthday();
        // SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String issuedDate = "";
        String issuedPlace = "";
        // String issuedDate=sdf.format(birthday);
        // String issuedPlace=xUserInfo.getPlace();
        Long collectAmount = xRecordRepayment.getRepaymentAmount();
        String expireDate = ""; // Optional
        String orderId = xRecordLoan.getOrderId();

        ValVirtualAccountReq req = new ValVirtualAccountReq();
        req.setRequestId(requestId);
        req.setRequestTime(requestTime);
        req.setPartnerCode(partnerCode);
        req.setOperation(operation);
        req.setAccName(accName);
        req.setClientIdNo(clientIdNo);
        req.setIssuedDate("");
        req.setIssuedPlace("");
        req.setCollectAmount(collectAmount.toString());
        req.setExpireDate(expireDate);
        req.setAccountType(accountType);
        req.setOrderId(orderId);

        String encryptStr = requestId + "|" + requestTime + "|" + partnerCode + "|" + operation + "|" + accName + "|"
            + clientIdNo + "|" + issuedDate + "|" + issuedPlace + "|" + collectAmount + "|" + expireDate + "|"
            + accountType + "|" + orderId;

        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);

        ValVirtualAccountRsp valVirtualAccountRsp = callCollectionApi(req);
        return valVirtualAccountRsp;
    }

    private ValVirtualAccountRsp callCollectionApi(ValVirtualAccountReq valVirtualAccountReq) {
        ValVirtualAccountRsp valVirtualAccountRsp = JsonUtils
            .jsonToPojo(HttpClientUtils.postForObject(collectionUrl, valVirtualAccountReq), ValVirtualAccountRsp.class);
        loggerCollectionInfo(valVirtualAccountReq, valVirtualAccountRsp);
        return valVirtualAccountRsp;
    }

    /**
     * 调用创建虚拟账号接口返回信息
     */
    private void loggerCollectionInfo(ValVirtualAccountReq valVirtualAccountReq,
        ValVirtualAccountRsp valVirtualAccountRsp) {
        // todo 需要确认调用接口超时是否是valVirtualAccountRsp==null
        if (valVirtualAccountRsp == null) {
            valVirtualAccountRsp = new ValVirtualAccountRsp();
            valVirtualAccountRsp.setResponseCode(BaokimResponseStatus.TIMEOUT.getCode());
            valVirtualAccountRsp.setResponseMessage("创建/更新虚拟账号接口请求超时，没有返回");
            valVirtualAccountRsp.setRequestId(valVirtualAccountReq.getRequestId());
            logger.info("创建/更新虚拟账号接口请求超时，没有返回");
        }

    }

    @Override
    public XVirtualAccount selectByOrderId(String orderId) {
        return xVirtualAccountDao.selectByOrderId(orderId);
    }

    @Override
    public ValVirtualAccountRsp updateXVirtualAccount(XVirtualAccount xVirtualAccount, Long collectAmount) {
        String requestId = UUIDUtils.getRequestId();
        String requestTime = UUIDUtils.getRequestTime();
        String partnerCode = apiParterCode;
        String operation = SysVariable.API_OPERATION_UPDATE_VIRTUALACCOUNT;
        String clientIdNo = xVirtualAccount.getClientidNo();
        String accountType = SysVariable.API_ACC_TYPE_SECOND; // 2-根据orderId

        // String orderId //订单id

        String accName = xVirtualAccount.getAccName();
        if (accName != null) {
            accName = accName.trim();
        }
        String issuedDate = "";
        String issuedPlace = "";
        String expireDate = ""; // Optional
        String orderId = xVirtualAccount.getOrderId();
        String accNo = xVirtualAccount.getAccNo();

        ValVirtualAccountReq req = new ValVirtualAccountReq();
        req.setRequestId(requestId);
        req.setRequestTime(requestTime);
        req.setPartnerCode(partnerCode);
        req.setOperation(operation);
        req.setAccNo(accNo);
        req.setAccName(accName);
        req.setClientIdNo(clientIdNo);
        req.setIssuedDate("");
        req.setIssuedPlace("");
        req.setCollectAmount(collectAmount.toString());
        req.setExpireDate(expireDate);
        req.setAccountType(accountType);
        req.setOrderId(orderId);

        String encryptStr = requestId + "|" + requestTime + "|" + partnerCode + "|" + accNo + "|" + accName + "|"
            + operation + "|" + issuedDate + "|" + issuedPlace + "|" + collectAmount + "|" + expireDate + "|"
            + clientIdNo + "|" + orderId;

        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);

        ValVirtualAccountRsp valVirtualAccountRsp = callCollectionApi(req);
        return valVirtualAccountRsp;
    }

    public ValVirtualAccountRsp autoAddVirtualAccount(String accName, String orderId, String userGid,
        Long collectAmount) {
        String requestId = UUIDUtils.getRequestId();
        String requestTime = UUIDUtils.getRequestTime();
        String partnerCode = apiParterCode;
        String operation = SysVariable.API_OPERATION_REGISTER_VIRTUALACCOUNT;
        String clientIdNo = MsgAuthCode.getAuthNineCode();
        String accountType = SysVariable.API_ACC_TYPE_SECOND; // 2-根据orderId

        if (accName != null) {
            accName = accName.trim();
        } else {
            accName = "";
        }
        String issuedDate = "";
        String issuedPlace = "";
        String expireDate = ""; // Optional

        ValVirtualAccountReq req = new ValVirtualAccountReq();
        req.setRequestId(requestId);
        req.setRequestTime(requestTime);
        req.setPartnerCode(partnerCode);
        req.setOperation(operation);
        req.setAccName(accName);
        req.setClientIdNo(clientIdNo);
        req.setIssuedDate("");
        req.setIssuedPlace("");
        req.setCollectAmount(collectAmount.toString());
        req.setExpireDate(expireDate);
        req.setAccountType(accountType);
        req.setOrderId(orderId);

        String encryptStr = requestId + "|" + requestTime + "|" + partnerCode + "|" + operation + "|" + accName + "|"
            + clientIdNo + "|" + issuedDate + "|" + issuedPlace + "|" + collectAmount + "|" + expireDate + "|"
            + accountType + "|" + orderId;

        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);

        ValVirtualAccountRsp valVirtualAccountRsp = callCollectionApi(req);
        return valVirtualAccountRsp;
    }

    @Override
    public boolean saveVirtualAccount() {
        try {
            List<XRecordLoanInfo> recordLoanInfos = xRecordLoanDao.findRecordLoanInfo();
            logger.info("需要自动创建的虚拟账号的个数为：" + recordLoanInfos.size() + "-----------");
            if (recordLoanInfos.size() == 0) {
                logger.info("当前列表没有用户需要生成虚拟账号");
            }

            int i = 0;
            for (XRecordLoanInfo xRecordLoanInfo : recordLoanInfos) {
                String userGid = xRecordLoanInfo.getUserGid();
                String orderId = xRecordLoanInfo.getOrderId();
                String accName = xRecordLoanInfo.getName();
                Long overdue_fee = xRecordLoanInfo.getOverdueFee();
                Long overdue_interest = xRecordLoanInfo.getOverdueInterest();
                Long due_amount = xRecordLoanInfo.getDueAmount();
                Long collectAmount = overdue_fee + overdue_interest + due_amount;

                // 查询虚拟账号有没有被创建了，如果创建了就不需要再创建了
                XVirtualAccount xVirtualAccount = selectByOrderId(orderId);

                if (xVirtualAccount == null) {
                    i++;
                    ValVirtualAccountRsp valVirtualAccountRsp =
                        autoAddVirtualAccount(accName, orderId, userGid, collectAmount);
                    String responseCode = valVirtualAccountRsp.getResponseCode();
                    String responseMsg = valVirtualAccountRsp.getResponseMessage();
                    xVirtualAccount = new XVirtualAccount();

                    if (BaokimResponseStatus.CollectionSuccess.getCode().equals(responseCode)) {
                        // 保存虚拟账号信息
                        xVirtualAccount.setAccName(valVirtualAccountRsp.getAccName());
                        xVirtualAccount.setAccNo(valVirtualAccountRsp.getAccNo());
                        xVirtualAccount.setClientidNo(valVirtualAccountRsp.getClientIdNo());
                        xVirtualAccount.setIssuedDate(valVirtualAccountRsp.getIssuedDate());
                        xVirtualAccount.setIssuedPlace(valVirtualAccountRsp.getIssuedPlace());
                        if (valVirtualAccountRsp.getCollectAmount() != null) {
                            xVirtualAccount.setCollectAmount(Integer.parseInt(valVirtualAccountRsp.getCollectAmount()));
                        }
                        xVirtualAccount.setOrderId(orderId);
                        xVirtualAccount.setExpireDate(valVirtualAccountRsp.getExpireDate());
                        xVirtualAccount.setAccountType(Integer.parseInt(valVirtualAccountRsp.getAccountType()));
                        xVirtualAccount.setCreateTime(new Date());
                        xVirtualAccount.setUpdateTime(new Date());
                        xVirtualAccount.setEnableFlag("Y");
                        xVirtualAccount.setRequestId(valVirtualAccountRsp.getRequestId());
                        xVirtualAccount.setResponseCode(responseCode);
                        xVirtualAccount.setResponseMessage(responseMsg);
                        xVirtualAccountDao.insert(xVirtualAccount);

                        logger.info("创建虚拟账号成功");
                        sysLogService.save(userGid, null, "还款-自动创建虚拟账号", true, responseMsg, responseCode);
                    } else {
                        logger.info("创建虚拟账号失败，返回状态码：{}，错误信息：{}", responseCode, responseMsg);
                        sysLogService.save(userGid, null, "还款-自动创建虚拟账号", false, responseMsg, responseCode);
                        xVirtualAccount.setRequestId(valVirtualAccountRsp.getRequestId());
                        xVirtualAccount.setResponseCode(responseCode);
                        xVirtualAccount.setResponseMessage(responseMsg);
                        xVirtualAccount.setCreateTime(new Date());
                        xVirtualAccount.setUpdateTime(new Date());
                        xVirtualAccount.setEnableFlag("Y");
                        xVirtualAccountDao.insert(xVirtualAccount);
                    }

                }

            }
            logger.info("创建虚拟账号的总数：" + i + "-----------------------------");
        } catch (NumberFormatException e) {
            logger.error("自动创建虚拟账号出错，错误信息为：" + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;

    }

}
