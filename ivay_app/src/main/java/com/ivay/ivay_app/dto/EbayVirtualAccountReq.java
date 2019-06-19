package com.ivay.ivay_app.dto;

import lombok.Data;

/**
 * ebay还款请求参数
 * @author panshuhua
 */
@Data
public class EbayVirtualAccountReq {
	private String pcode;
    private String merchant_code;
    private String data;  //请求参数经过加密后的字符串
} 
