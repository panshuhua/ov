package com.ivay.ivay_repository.model;

import java.util.Date;

import lombok.Data;

/**
 * baokim系统导出的借款记录
 * 
 * @author panshuhua
 * @date 2019/07/03
 */
@Data
public class BaokimTransferData {
    private String transTime;
    private String baokimTransId;
    private String amount;
    private String transferRealAmount;
    private String cardNo;
    private String customerName;
    private String status;

    private Integer id;
    private Date createTime;
    private Date updateTime;
    private String enableFlag;
}
