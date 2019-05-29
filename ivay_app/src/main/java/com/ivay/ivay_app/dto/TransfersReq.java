package com.ivay.ivay_app.dto;

import java.io.Serializable;

public class TransfersReq implements Serializable {

    private static final long serialVersionUID = 35435494737580569L;

    // 数字校验
    private String RequestId;
    private String RequestTime;
    private String PartnerCode;
    private String Operation;
    private String BankNo;  // 银行代码
    private String AccNo;  // 借款方卡号
    private String AccType;
    private String Signature;
    // 交易
    private String ReferenceId;
    private long RequestAmount;
    private String Memo;

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getReferenceId() {
        return ReferenceId;
    }

    public void setReferenceId(String referenceId) {
        ReferenceId = referenceId;
    }

    public long getRequestAmount() {
        return RequestAmount;
    }

    public void setRequestAmount(long requestAmount) {
        RequestAmount = requestAmount;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getRequestTime() {
        return RequestTime;
    }

    public void setRequestTime(String requestTime) {
        RequestTime = requestTime;
    }

    public String getPartnerCode() {
        return PartnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        PartnerCode = partnerCode;
    }

    public String getOperation() {
        return Operation;
    }

    public void setOperation(String operation) {
        Operation = operation;
    }

    public String getBankNo() {
        return BankNo;
    }

    public void setBankNo(String bankNo) {
        BankNo = bankNo;
    }

    public String getAccNo() {
        return AccNo;
    }

    public void setAccNo(String accNo) {
        AccNo = accNo;
    }

    public String getAccType() {
        return AccType;
    }

    public void setAccType(String accType) {
        AccType = accType;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        Signature = signature;
    }

}
