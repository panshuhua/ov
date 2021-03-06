package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.TransfersReq;
import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.XAPIService;
import com.ivay.ivay_common.utils.*;
import com.ivay.ivay_repository.dao.master.XBaokimTransfersInfoDao;
import com.ivay.ivay_repository.model.XBaokimTransfersInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class XAPIServiceImpl implements XAPIService {
    private static final Logger logger = LoggerFactory.getLogger(XAPIService.class);

    @Resource
    private XBaokimTransfersInfoDao xBaokimTransfersInfoDao;

    @Value("${api_transfer_url}")
    private String transferUrl;

    @Value("${api_partner_code}")
    public String api_partner_code;

    @Override
    public TransfersRsp validateCustomerInformation(String bankNo, String accNo, String accType) {
        TransfersReq transfersReq = new TransfersReq();
        transfersReq.setRequestId(UUIDUtils.getRequestId());
        transfersReq.setRequestTime(UUIDUtils.getRequestTime());
        transfersReq.setPartnerCode(api_partner_code);
        transfersReq.setOperation(SysVariable.API_OPERATION_VALIDATE);
        transfersReq.setBankNo(bankNo);
        transfersReq.setAccNo(accNo);
        transfersReq.setAccType(accType);
        String encryptStr = transfersReq.getRequestId() + "|"
                + transfersReq.getRequestTime() + "|"
                + transfersReq.getPartnerCode() + "|"
                + transfersReq.getOperation() + "|"
                + transfersReq.getBankNo() + "|"
                + transfersReq.getAccNo() + "|"
                + transfersReq.getAccType();
        String signature = RSAEncryptSha1.encrypt2Sha1(encryptStr);
        transfersReq.setSignature(signature);
        return callTransfersApi(transferUrl, transfersReq, null);
    }

    private TransfersRsp callTransfersApi(String url, TransfersReq transfersReq, String loanGid) {
        TransfersRsp transfersRsp = JsonUtils.jsonToPojo(HttpClientUtils.postForObject(transferUrl, transfersReq), TransfersRsp.class);
        loggerTransferInfo(transfersReq, transfersRsp, loanGid);
        return transfersRsp;
    }

    /**
     * 调用接口交易接口时，需要记录请求信息
     *
     * @param transfersReq
     * @param transfersRsp
     * @param loanGid
     */
    private void loggerTransferInfo(TransfersReq transfersReq, TransfersRsp transfersRsp, String loanGid) {
        XBaokimTransfersInfo xBaokimTransfersInfo = new XBaokimTransfersInfo();
        // 保存 request
        xBaokimTransfersInfo.setRequestId(transfersReq.getRequestId());
        xBaokimTransfersInfo.setRequestTime(transfersReq.getRequestTime());
        xBaokimTransfersInfo.setPartnerCode(transfersReq.getPartnerCode());
        xBaokimTransfersInfo.setOperation(transfersReq.getOperation());
        xBaokimTransfersInfo.setMemo(transfersReq.getMemo());

        // todo 需要确认调用接口超时是transfersRsp==null？
        if (transfersRsp == null) {
            transfersRsp = new TransfersRsp();
            transfersRsp.setResponseCode(BaokimResponseStatus.TIMEOUT.getCode());
            transfersRsp.setResponseMessage("交易接口请求超时，没有返回");
            transfersRsp.setReferenceId(transfersReq.getReferenceId());
            logger.info("交易接口请求超时，没有返回");
        } else {
            xBaokimTransfersInfo.setTransactionId(transfersRsp.getTransactionId());
            xBaokimTransfersInfo.setTransactionTime(transfersRsp.getTransactionTime());
            xBaokimTransfersInfo.setBankNo(transfersRsp.getBankNo());
            xBaokimTransfersInfo.setAccNo(transfersRsp.getAccNo());
            xBaokimTransfersInfo.setAccName(transfersRsp.getAccName());
            xBaokimTransfersInfo.setAccType(transfersRsp.getAccType());
            xBaokimTransfersInfo.setRequestAmount(transfersRsp.getRequestAmount());
            xBaokimTransfersInfo.setTransferAmount(transfersRsp.getTransferAmount());
        }
        xBaokimTransfersInfo.setLoanGid(loanGid);
        xBaokimTransfersInfo.setResponseCode(transfersRsp.getResponseCode());
        xBaokimTransfersInfo.setResponseMessage(transfersRsp.getResponseMessage());
        xBaokimTransfersInfo.setReferenceId(transfersRsp.getReferenceId());
        xBaokimTransfersInfoDao.save(xBaokimTransfersInfo);
    }

    @Override
    public TransfersRsp transfers(String bankNo, String accNo, long requestAmount, String memo, String accType, String loanGid) {
        TransfersReq transfersReq = new TransfersReq();
        String referenceId = UUIDUtils.getUUID();
        transfersReq.setRequestId(UUIDUtils.getRequestId());
        transfersReq.setRequestTime(UUIDUtils.getRequestTime());
        transfersReq.setPartnerCode(api_partner_code);
        transfersReq.setOperation(SysVariable.API_OPERATION_TRANSFER);
        transfersReq.setReferenceId(referenceId);
        transfersReq.setBankNo(bankNo);
        transfersReq.setAccNo(accNo);
        transfersReq.setAccType(accType);
        transfersReq.setRequestAmount(requestAmount);
        transfersReq.setMemo(memo);
        String encryptStr = transfersReq.getRequestId() + "|"
                + transfersReq.getRequestTime() + "|"
                + transfersReq.getPartnerCode() + "|"
                + transfersReq.getOperation() + "|"
                + transfersReq.getReferenceId() + "|"
                + transfersReq.getBankNo() + "|"
                + transfersReq.getAccNo() + "|"
                + transfersReq.getAccType() + "|"
                + transfersReq.getRequestAmount() + "|"
                + transfersReq.getMemo();
        String signature = RSAEncryptSha1.encrypt2Sha1(encryptStr);
        transfersReq.setSignature(signature);
        return callTransfersApi(transferUrl, transfersReq, loanGid);
    }

    @Override
    public TransfersRsp transfersInfo(String referenceId, String loanGid) {
        TransfersReq transfersReq = new TransfersReq();
        transfersReq.setRequestId(UUIDUtils.getRequestId());
        transfersReq.setRequestTime(UUIDUtils.getRequestTime());
        transfersReq.setPartnerCode(api_partner_code);
        transfersReq.setOperation(SysVariable.API_OPERATION_TRANSFER_INFO);
        transfersReq.setReferenceId(referenceId);
        String encryptStr = transfersReq.getRequestId() + "|"
                + transfersReq.getRequestTime() + "|"
                + transfersReq.getPartnerCode() + "|"
                + transfersReq.getOperation() + "|"
                + transfersReq.getReferenceId();
        String signature = RSAEncryptSha1.encrypt2Sha1(encryptStr);
        transfersReq.setSignature(signature);
        return callTransfersApi(transferUrl, transfersReq, loanGid);
    }

    @Override
    public TransfersRsp balance() {
        TransfersReq transfersReq = new TransfersReq();
        transfersReq.setRequestId(UUIDUtils.getRequestId());
        transfersReq.setRequestTime(UUIDUtils.getRequestTime());
        transfersReq.setPartnerCode(api_partner_code);
        transfersReq.setOperation(SysVariable.API_OPERATION_BALANCE);
        String encryptStr = transfersReq.getRequestId() + "|"
                + transfersReq.getRequestTime() + "|"
                + transfersReq.getPartnerCode() + "|"
                + transfersReq.getOperation();
        String signature = RSAEncryptSha1.encrypt2Sha1(encryptStr);
        transfersReq.setSignature(signature);
        return callTransfersApi(transferUrl, transfersReq, null);
    }

    /**
     * 判断 是否有足够的余额借款
     *
     * @return -1表示接口调用失败
     */
    @Override
    public long getCanborrowBalance() {
        long result = -1;
        TransfersRsp transfersRsp = balance();
        if (BaokimResponseStatus.SUCCESS.getCode().equals(transfersRsp.getResponseCode())) {
            long availableAmount = Long.parseLong(transfersRsp.getAvailable());
            long pendingAmount = Long.parseLong(transfersRsp.getHolding());
            if (availableAmount >= pendingAmount) {
                result = availableAmount - pendingAmount;
            } else {
                result = 0;
            }
        }
        return result;
    }
}
