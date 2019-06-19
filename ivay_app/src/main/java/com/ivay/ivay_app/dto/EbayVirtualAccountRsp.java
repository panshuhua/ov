package com.ivay.ivay_app.dto;

import lombok.Data;

/**
 * ebay还款响应参数
 * @author panshuhua
 */
@Data
public class EbayVirtualAccountRsp {
    private String response_code;
    private String message;
    private String account_no;
    private String account_name;
    private String bank_code;
    private String bank_name;
    private String map_id;
}
