package com.ivay.ivay_repository.model;

import lombok.Data;

import java.util.Date;

/**
 * descripiton:
 *
 * @author: xx
 * @date: 2019/1/31
 * @time: 21:42
 * @modifier:
 * @since:
 */
@Data
public class RiskUser {

    private Integer id;
    
    private String phone;

    private String amount;

    private Date importTime;

    private Integer salesmanId;

    private Integer assignStatus;

    private Date assignTime;

    private Integer dealStatus;

}
