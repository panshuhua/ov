package com.ivay.ivay_repository.dto;

import java.util.Date;

import lombok.Data;

@Data
public class XOverDueFee {
    private Long dueAmount;
    private String fmcToken;
    private String phone;
    private Date dueTime;
}
