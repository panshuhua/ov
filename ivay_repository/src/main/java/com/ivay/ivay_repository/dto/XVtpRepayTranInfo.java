package com.ivay.ivay_repository.dto;

import java.util.Date;

import lombok.Data;

/**
 * vtp还款交易数据
 * 
 * @author panshuhua
 * @date 2019/07/19
 */
@Data
public class XVtpRepayTranInfo {
    private String orderId;
    private Long shouldRepayAmount;
    private String phone;
    private String name;
    private String identityCard;
    private Date birthday;
    private String sex;
    private String place;
}
