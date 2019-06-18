package com.ivay.ivay_repository.model;

import lombok.Data;

/**
 * ebay还款请求响应信息
 * @author panshuhua
 */
@Data
public class XEbayVirtualAccount {
	//request Data
    private String pcode;
    private String merchant_code;
    private XEbayData data;
}
