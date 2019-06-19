package com.ivay.ivay_repository.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * ebay还款请求响应信息
 * @author panshuhua
 */
@Data
public class XEbayVirtualAccount {
    private String pcode;
    private String merchantCode;
    private String mapId;
    private BigDecimal amount;
    private String startDate;
    private String endDate;
    private String condition;
    private String customerName;
    private String requestId;
    private String responseCode;
    private String message;
    private String accountNo;
    private String accountName;
    private String bankCode;
    private String bankName;
}
