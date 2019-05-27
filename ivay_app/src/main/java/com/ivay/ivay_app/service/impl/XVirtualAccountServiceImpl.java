package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dao.TokenDao;
import com.ivay.ivay_app.dao.XUserInfoDao;
import com.ivay.ivay_app.dao.XVirtualAccountDao;
import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.ValVirtualAccountReq;
import com.ivay.ivay_app.dto.ValVirtualAccountRsp;
import com.ivay.ivay_app.model.XRecordLoan;
import com.ivay.ivay_app.model.XRecordRepayment;
import com.ivay.ivay_app.model.XUserInfo;
import com.ivay.ivay_app.model.XVirtualAccount;
import com.ivay.ivay_app.service.XVirtualAccountService;
import com.ivay.ivay_app.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class XVirtualAccountServiceImpl implements XVirtualAccountService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    TokenDao tokenDao;
    @Autowired
    XVirtualAccountDao xVirtualAccountDao;
    @Autowired
    XUserInfoDao xUserInfoDao;

    @Value("${api_collection_url}")
    private String collectionUrl;

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
        String partnerCode = SysVariable.API_PARTNER_CODE;
        String operation = SysVariable.API_OPERATION_REGISTER_VIRTUALACCOUNT;
        String clientIdNo = MsgAuthCode.getAuthNineCode();
        String accountType = SysVariable.API_ACC_TYPE_SECOND; //2-根据orderId

//    	String orderId //订单id
        //根据usergid查询用户信息
        XUserInfo xUserInfo = xUserInfoDao.getByGid(xRecordLoan.getUserGid());  //accountType=1时需要

        String accName = xUserInfo.getName();
//    	Date birthday=xUserInfo.getBirthday();
//    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String issuedDate = "";
        String issuedPlace = "";
//    	String issuedDate=sdf.format(birthday);
//    	String issuedPlace=xUserInfo.getPlace();
        Long collectAmount = xRecordRepayment.getRepaymentAmount();
        String expireDate = new SimpleDateFormat("yyyy-MM-dd").format(xRecordLoan.getDueTime());  //Optional
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

        String encryptStr = requestId + "|" + requestTime + "|" + partnerCode + "|" + operation + "|" + accName +
                "|" + clientIdNo + "|" + issuedDate + "|" + issuedPlace + "|" + collectAmount + "|" + expireDate
                + "|" + accountType + "|" + orderId;

        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);

        ValVirtualAccountRsp valVirtualAccountRsp = callCollectionApi(req);
        return valVirtualAccountRsp;
    }

    private ValVirtualAccountRsp callCollectionApi(ValVirtualAccountReq valVirtualAccountReq) {
        ValVirtualAccountRsp valVirtualAccountRsp = JsonUtils.jsonToPojo(HttpClientUtils.postForObject(collectionUrl, valVirtualAccountReq), ValVirtualAccountRsp.class);
        loggerCollectionInfo(valVirtualAccountReq, valVirtualAccountRsp);
        return valVirtualAccountRsp;
    }

    /**
     * 调用创建虚拟账号接口返回信息
     */
    private void loggerCollectionInfo(ValVirtualAccountReq valVirtualAccountReq, ValVirtualAccountRsp valVirtualAccountRsp) {

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
	        String partnerCode = SysVariable.API_PARTNER_CODE;
	        String operation = SysVariable.API_OPERATION_UPDATE_VIRTUALACCOUNT;
	        String clientIdNo = xVirtualAccount.getClientidNo();
	        String accountType = SysVariable.API_ACC_TYPE_SECOND;  //2-根据orderId

//	    	String orderId //订单id

	        String accName = xVirtualAccount.getAccName();
	        String issuedDate = "";
	        String issuedPlace = "";
	        String expireDate = xVirtualAccount.getExpireDate();  //Optional
	        String orderId = xVirtualAccount.getOrderId();
	        String accNo=xVirtualAccount.getAccNo();

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

	        String encryptStr = requestId + "|" + requestTime + "|"+ partnerCode + "|" + accNo + "|" + accName + "|" + operation +
	                "|" + issuedDate + "|" + issuedPlace + "|" + collectAmount + "|" + expireDate+ "|" + clientIdNo + "|" + orderId ;

	        System.out.println("加密前：" + encryptStr);
	        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
	        req.setSignature(signature);

	        ValVirtualAccountRsp valVirtualAccountRsp = callCollectionApi(req);
	        return valVirtualAccountRsp;
	}


}
