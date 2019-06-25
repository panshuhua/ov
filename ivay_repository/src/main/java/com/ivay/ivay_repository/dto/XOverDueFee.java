package com.ivay.ivay_repository.dto;

import lombok.Data;

@Data
public class XOverDueFee {
    private Long dueAmount;
    private String fmcToken;
    private String phone;
}
