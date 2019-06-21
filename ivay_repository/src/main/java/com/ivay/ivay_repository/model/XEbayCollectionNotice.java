package com.ivay.ivay_repository.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class XEbayCollectionNotice {
    private String requestId;
    private String requestTime;
    private String bankTranTime;
    private String referenceId;
    private String mapId;
    private BigDecimal amount;
    private String merchantCode;
    private BigDecimal fee;
    private String vaName;
    private String vaAcc;
    private String bankCode;
    private String bankName;
    private String responseCode;
    private String responseMessage;
    
    private Long id;
    private Date createTime;
    private Date updateTime;
}
