package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.CollectionTransactionNotice;
import com.ivay.ivay_app.dto.CollectionTransactionRsp;
import com.ivay.ivay_app.service.XCollectionTransactionService;
import com.ivay.ivay_app.service.XRecordRepaymentService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.RSAEncryptShaCollection;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_repository.dao.master.TokenDao;
import com.ivay.ivay_repository.dao.master.XCollectionTransactionDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.XCollectionTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class XCollectionTransactionServiceImpl implements XCollectionTransactionService {
    @Autowired
    TokenDao tokenDao;
    @Autowired
    XCollectionTransactionDao xCollectionTransactionDao;
    @Autowired
    private XRecordRepaymentService xRecordRepaymentService;
    @Autowired
    private XUserInfoDao xUserInfoDao;

    @Override
    public String getRequestId(String PartnerCode, String date) {
        String uniqueId = tokenDao.getUniqueId();
        return PartnerCode + "BK" + date + uniqueId;
    }

    @Override
    public boolean checkRequestId(String requestId) {
        String reqId = xCollectionTransactionDao.findRequestId(requestId);
        if (!StringUtils.isEmpty(reqId)) {
            return true;
        }
        return false;
    }

    @Override
    public int insert(XCollectionTransaction xCollectionTransaction) {
        return xCollectionTransactionDao.insert(xCollectionTransaction);
    }

    @Override
    public long getCollectAmount(String accNo) {
        return xCollectionTransactionDao.getCollectAmount(accNo);
    }

    @Override
    public CollectionTransactionRsp noticeCollection(
            CollectionTransactionNotice notice) throws ParseException {
        String ResponseCode = BaokimResponseStatus.CollectionSuccess.getCode();
        String ResponseMessage = BaokimResponseStatus.CollectionSuccess.getMessage();
        String RequestId = notice.getRequestId();
        String RequestTime = notice.getRequestTime();
        String PartnerCode = notice.getPartnerCode();
        String AccNo = notice.getAccNo();
        String ClientIdNo = notice.getClientIdNo();
        String TransId = notice.getTransId();
        String TransAmount = notice.getTransAmount();
        String TransTime = notice.getTransTime();
        String BefTransDebt = notice.getBefTransDebt();
        String AffTransDebt = notice.getAffTransDebt();

        String AccountType = notice.getAccountType();
        if (AccountType == null) {
            AccountType = "";
        }
        String OrderId = notice.getOrderId();
        if (OrderId == null) {
            OrderId = "";
        }

        String Signature = notice.getSignature();

        CollectionTransactionRsp rsp = new CollectionTransactionRsp();
        rsp.setAccNo(notice.getAccNo());
        rsp.setAffTransDebt(notice.getAffTransDebt());
        Date reqTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(RequestTime);
        String RequestDate = new SimpleDateFormat("yyyyMMdd").format(reqTime);
        String ReferenceId = PartnerCode + "BK" + RequestDate + "00" + MsgAuthCode.getAuthCode();
        System.out.println("ReferenceId=" + ReferenceId);
        rsp.setReferenceId(ReferenceId);
        String rspEncryptStr = ResponseCode + "|" + ResponseMessage + "|" + ReferenceId + "|" +
                AccNo + "|" + AffTransDebt;
        System.out.println("返回给baokim的签名明文=" + rspEncryptStr);

        String rspSignature = RSAEncryptShaCollection.encrypt2Sha1(rspEncryptStr);
        rsp.setSignature(rspSignature);

        //校验各个字段
        //RequestId查重
        boolean existsRequestId = checkRequestId(RequestId);
        if (existsRequestId) {
            ResponseCode = BaokimResponseStatus.IncorrectRequestId.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectRequestId.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        boolean isCorrectRequestTime = DateUtils.verifyDateTime(RequestTime);
        if (!isCorrectRequestTime) {
            ResponseCode = BaokimResponseStatus.IncorrectRequestId.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectRequestId.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        boolean isCorrect = StringUtil.isNumeric(TransAmount);
        if (!isCorrect) {
            ResponseCode = BaokimResponseStatus.IncorrectTransAmount.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectTransAmount.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        //根据accNo查询虚拟账号中的collect_amount，判断TransAmount是否与之一致（假设是一次还清）
        //现可支持多次还款
//        long collectAmount=getCollectAmount(AccNo);
//        long transAmount=Long.parseLong(TransAmount);
//        if(transAmount!=collectAmount){
//        	ResponseCode=BaokimResponseStatus.IncorrectTransAmount.getCode();
//        	ResponseMessage=BaokimResponseStatus.IncorrectTransAmount.getMessage();
//        	setRsp(rsp,ResponseCode,ResponseMessage);
//        	return rsp;
//        }

        isCorrect = StringUtil.isNumeric(TransAmount);
        if (!isCorrect) {
            ResponseCode = BaokimResponseStatus.IncorrectTransAmount.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectTransAmount.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        isCorrect = StringUtil.isNumeric(BefTransDebt);
        if (!isCorrect) {
            ResponseCode = BaokimResponseStatus.IncorrectBefTransDebt.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectBefTransDebt.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        isCorrect = StringUtil.isNumeric(AffTransDebt);
        if (!isCorrect) {
            ResponseCode = BaokimResponseStatus.IncorrectAffTransDebt.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectAffTransDebt.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            rsp.setSignature("");
            return rsp;
        }

        boolean isCorrectTime = DateUtils.verifyDateTime(TransTime);
        if (!isCorrectTime) {
            ResponseCode = BaokimResponseStatus.IncorrectTransTime.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectTransTime.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }
        boolean hasSpecialChar = StringUtil.hasSpecialChar(TransId);
        if (hasSpecialChar) {
            ResponseCode = BaokimResponseStatus.IncorrectTransId.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectTransId.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        String encryptStr = RequestId + "|" + RequestTime + "|" + PartnerCode + "|" + AccNo +
                "|" + ClientIdNo + "|" + TransId + "|" + TransAmount + "|" + TransTime + "|" +
                BefTransDebt + "|" + AffTransDebt + "|" + AccountType + "|" + OrderId;

        System.out.println("请求的签名明文：" + encryptStr);
        System.out.println("请求发送的签名：" + Signature);

        //验证签名认证
        boolean b = RSAEncryptShaCollection.decrypt2Sha1(encryptStr, Signature);
        System.out.println("签名校验结果：" + b);

        if (!b) {
            ResponseCode = BaokimResponseStatus.IncorrectSignature.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectSignature.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        //请求字段存入数据库
        XCollectionTransaction xCollectionTransaction = new XCollectionTransaction();
        xCollectionTransaction.setRequestId(RequestId);
        xCollectionTransaction.setRequestTime(RequestTime);
        xCollectionTransaction.setPartnerCode(PartnerCode);
        xCollectionTransaction.setAccNo(AccNo);
        xCollectionTransaction.setClientidNo(ClientIdNo);
        xCollectionTransaction.setTransId(TransId);
        xCollectionTransaction.setTransAmount(TransAmount);
        xCollectionTransaction.setBeftransDebt(BefTransDebt);
        xCollectionTransaction.setAfftransDebt(AffTransDebt);
        xCollectionTransaction.setAccountType(AccountType);
        xCollectionTransaction.setTransTime(TransTime);
        xCollectionTransaction.setOrderId(OrderId);
        xCollectionTransaction.setCreateTime(new Date());
        insert(xCollectionTransaction);

        setRsp(rsp, ResponseCode, ResponseMessage);

        //更新数据库中的数据-借款
      /*  if(!StringUtils.isEmpty(OrderId)){
        	//根据orderId查询user_gid
        	XRecordLoan xRecordLoan=xUserInfoDao.getUserGidByOrderId(OrderId);
        	String userGid=xRecordLoan.getUserGid();
        	//更新还款记录-存在问题
        	//xRecordRepaymentService.confirmRepayment(xRecordLoan, xRecordRepayment);
        }*/


        return rsp;
    }

    private void setRsp(CollectionTransactionRsp rsp, String ResponseCode, String ResponseMessage) {
        rsp.setResponseCode(ResponseCode);
        rsp.setResponseMessage(ResponseMessage);
    }

}
