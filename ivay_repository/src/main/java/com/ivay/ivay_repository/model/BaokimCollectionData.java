package com.ivay.ivay_repository.model;

import java.util.Date;

import lombok.Data;

/**
 * baokim系统导出的还款交易数据
 * 
 * @author panshuhua
 * @date 2019/07/01
 */
@Data
public class BaokimCollectionData {
    private String transactionIdBaokim;
    private String timeRecorded;
    private String accountNo;
    private String amount;
    private String accountName;
    private String orderId;
    private String accountType;
    private String status;

    private Integer id;
    private Date createTime;
    private Date updateTime;
    private String enableFlag;

}
