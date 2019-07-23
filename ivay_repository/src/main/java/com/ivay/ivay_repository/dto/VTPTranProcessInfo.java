package com.ivay.ivay_repository.dto;

import java.util.Date;

import lombok.Data;

/**
 * VTP交易信息-交易过程
 * 
 * @author panshuhua
 * @date 2019/07/22
 */
@Data
public class VTPTranProcessInfo {
    private Integer id;
    private String transId;
    private String refId;
    private String customerCode;
    private Long amount;
    private Long fee;
    private String content;
    private String agentUser;
    private String agentName;
    private Long transDate;
    private Date createTime;
    private Date updateTime;
    private String enableFlag;
    private String transMsg;
    private Integer transStatus;
}
