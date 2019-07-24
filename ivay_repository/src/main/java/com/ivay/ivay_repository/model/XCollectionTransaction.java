package com.ivay.ivay_repository.model;

import java.util.Date;

import lombok.Data;

@Data
public class XCollectionTransaction {
    private String requestId;
    private String requestTime;
    private String partnerCode;
    private String accNo;
    private String clientidNo;
    private String transId;
    private String transAmount;
    private String transTime;
    private String beftransDebt;
    private String afftransDebt;
    private String accountType;
    private String orderId;
    private Date createTime;
    private Date updateTime;
    private String enableFlag;
    private String referenceId;
}
