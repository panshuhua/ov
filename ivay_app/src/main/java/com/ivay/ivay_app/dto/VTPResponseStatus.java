package com.ivay.ivay_app.dto;

/**
 * VTP还款接口返回状态
 * 
 * @author panshuhua
 * @date 2019/07/22
 */
public enum VTPResponseStatus {
    InvalidPartnerCode(-2, "Invalid partner code"), InvalidSignature(-1, "Invalid signature"),
    Successful(0, "Successful"), LackOfData(1, "Lack of data"), WrongDataTransmission(2, "Wrong data transmission"),
    OverlappingTransaction(3, "Overlapping transaction"), InvalidTransaction(4, "Invalid transaction"),
    ExceedingExecutionTime(5, "Exceeding the execution time");

    VTPResponseStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
