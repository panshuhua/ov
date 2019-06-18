package com.ivay.ivay_repository.model;

import lombok.Data;

/**
 * ebay虚拟账号扩展信息
 * @author panshuhua
 */
@Data
public class XEbayExtend {
    private String phone;
    private String email;
    private String address;
    private String id;
}
