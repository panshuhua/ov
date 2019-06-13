package com.ivay.ivay_app.service.impl;

import com.alibaba.fastjson.JSON;
import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.EbayTransfersReq;
import com.ivay.ivay_app.dto.EbayTransfersRsp;
import com.ivay.ivay_app.service.XEbayAPIService;
import com.ivay.ivay_common.utils.HttpClientUtils;
import com.ivay.ivay_common.utils.RSAEncryptSha1;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_common.utils.UUIDUtils;
import com.ivay.ivay_repository.dao.master.XBaokimTransfersInfoDao;
import com.ivay.ivay_repository.model.XEbayTransfersInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class XEbayAPIServiceImpl implements XEbayAPIService {
    private static final Logger logger = LoggerFactory.getLogger(XAPIServiceImpl.class);

    @Resource
    private XBaokimTransfersInfoDao xBaokimTransfersInfoDao;

    @Value("${ebay_api_transfer_url}")
    private String transferUrl;

    @Value("${ebay_api_partner_code}")
    public String api_partner_code;

    @Override
    public EbayTransfersRsp validateCustomerInformation(String bankNo, String accNo, String accType) {
        EbayTransfersReq transfersReq = new EbayTransfersReq();
        transfersReq.setRequestId(UUIDUtils.getEbayRequestId());
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
        return callTransfersApi(transferUrl, transfersReq);
    }

    private EbayTransfersRsp callTransfersApi(String url, EbayTransfersReq transfersReq) {
        String json = HttpClientUtils.postForObject(transferUrl, transfersReq);
        EbayTransfersRsp transfersRsp = JSON.parseObject(json, EbayTransfersRsp.class);
        loggerTransferInfo(transfersReq, transfersRsp);
        return transfersRsp;
    }

    /**
     * 调用接口交易接口时，需要记录请求信息
     *
     * @param transfersReq
     * @param transfersRsp
     */
    private void loggerTransferInfo(EbayTransfersReq transfersReq, EbayTransfersRsp transfersRsp) {
        XEbayTransfersInfo xEbayTransfersInfo = new XEbayTransfersInfo();
        // 保存 request
        xEbayTransfersInfo.setRequestId(transfersReq.getRequestId());
        xEbayTransfersInfo.setRequestTime(transfersReq.getRequestTime());
        xEbayTransfersInfo.setPartnerCode(transfersReq.getPartnerCode());
        xEbayTransfersInfo.setOperation(transfersReq.getOperation());
        xEbayTransfersInfo.setMemo(transfersReq.getMemo());

        // todo 需要确认调用接口超时是transfersRsp==null？
        if (transfersRsp == null) {
            transfersRsp = new EbayTransfersRsp();
            transfersRsp.setResponseCode(BaokimResponseStatus.TIMEOUT.getCode());
            transfersRsp.setResponseMessage("交易接口请求超时，没有返回");
            transfersRsp.setReferenceId(transfersReq.getReferenceId());
            logger.info("交易接口请求超时，没有返回");
        } else {
            xEbayTransfersInfo.setTransactionId(transfersRsp.getTransactionId());
            xEbayTransfersInfo.setTransactionTime(transfersRsp.getTransactionTime());
            xEbayTransfersInfo.setBankNo(transfersRsp.getBankNo());
            xEbayTransfersInfo.setAccNo(transfersRsp.getAccNo());
            xEbayTransfersInfo.setAccName(transfersRsp.getAccName());
            xEbayTransfersInfo.setAccType(transfersRsp.getAccType());
            xEbayTransfersInfo.setRequestAmount(transfersRsp.getRequestAmount());
            xEbayTransfersInfo.setTransferAmount(transfersRsp.getTransferAmount());
        }
        xEbayTransfersInfo.setResponseCode(transfersRsp.getResponseCode());
        xEbayTransfersInfo.setResponseMessage(transfersRsp.getResponseMessage());
        xEbayTransfersInfo.setReferenceId(transfersRsp.getReferenceId());
        if (transfersRsp.getSubResponseCode() != null) {
            xEbayTransfersInfo.setSubErrorCode(transfersRsp.getSubResponseCode().getErrorCode());
            xEbayTransfersInfo.setSubErrorMessage(transfersRsp.getSubResponseCode().getMessage());
        }
        xEbayTransfersInfo.setReason(transfersRsp.getReason());
        xBaokimTransfersInfoDao.saveEbayInfo(xEbayTransfersInfo);
    }

    @Override
    public EbayTransfersRsp transfers(String bankNo, String accNo, long requestAmount, String memo, String accType,
                                      String accountName, String contractNumber, String extend) {
        EbayTransfersReq transfersReq = new EbayTransfersReq();
        String referenceId = UUIDUtils.getUUID();
        transfersReq.setRequestId(UUIDUtils.getEbayRequestId());
        transfersReq.setRequestTime(UUIDUtils.getRequestTime());
        transfersReq.setPartnerCode(api_partner_code);
        transfersReq.setOperation(SysVariable.API_OPERATION_TRANSFER);
        transfersReq.setReferenceId(referenceId);
        transfersReq.setBankNo(bankNo);
        transfersReq.setAccNo(accNo);
        transfersReq.setAccType(accType);
        transfersReq.setRequestAmount(requestAmount);
        transfersReq.setMemo(memo);
        //ebay特有的字段
        transfersReq.setAccountName(accountName);
        transfersReq.setContractNumber(contractNumber);
        transfersReq.setExtends(extend);
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
        return callTransfersApi(transferUrl, transfersReq);
    }

    @Override
    public EbayTransfersRsp transfersInfo(String referenceId) {
        EbayTransfersReq transfersReq = new EbayTransfersReq();
        transfersReq.setRequestId(UUIDUtils.getEbayRequestId());
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
        return callTransfersApi(transferUrl, transfersReq);
    }

    @Override
    public EbayTransfersRsp balance() {
        EbayTransfersReq transfersReq = new EbayTransfersReq();
        transfersReq.setRequestId(UUIDUtils.getEbayRequestId());
        transfersReq.setRequestTime(UUIDUtils.getRequestTime());
        transfersReq.setPartnerCode(api_partner_code);
        transfersReq.setOperation(SysVariable.API_OPERATION_BALANCE);
        String encryptStr = transfersReq.getRequestId() + "|"
                + transfersReq.getRequestTime() + "|"
                + transfersReq.getPartnerCode() + "|"
                + transfersReq.getOperation();
        String signature = RSAEncryptSha1.encrypt2Sha1(encryptStr);
        transfersReq.setSignature(signature);
        return callTransfersApi(transferUrl, transfersReq);
    }

}
