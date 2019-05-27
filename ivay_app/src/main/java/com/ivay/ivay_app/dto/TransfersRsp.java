package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TransfersRsp implements Serializable {

    private static final long serialVersionUID = 35435494737580569L;

    // 数字校验
    @JsonProperty("ResponseCode")
    private String responseCode;
    @JsonProperty("ResponseMessage")
    private String responseMessage;
    @JsonProperty("RequestId")
    private String requestId;
    @JsonProperty("BankNo")
    private String bankNo;
    @JsonProperty("AccNo")
    private String accNo;
    @JsonProperty("AccType")
    private String accType;
    @JsonProperty("AccName")
    private String accName;
    @JsonProperty("Signature")
    private String signature;
    // 交易
    @JsonProperty("ReferenceId")
    private String referenceId;
    @JsonProperty("TransactionId")
    private String transactionId;
    @JsonProperty("TransactionTime")
    private String transactionTime;
    @JsonProperty("RequestAmount")
    private String requestAmount;
    @JsonProperty("TransferAmount")
    private String transferAmount;
    // 余额
    @JsonProperty("PartnerCode")
    private String partnerCode;
    @JsonProperty("Available")
    private String available;
    @JsonProperty("Holding")
    private String holding;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(String requestAmount) {
        this.requestAmount = requestAmount;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getHolding() {
        return holding;
    }

    public void setHolding(String holding) {
        this.holding = holding;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
}
