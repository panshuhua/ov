package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("逾期还款信息")
public class XRepaymentAccountInfo {
    private String phone;
    private String name;
    private String orderId;
    private String loanTime;
    private String dueTime;
    private long loanAmount;
    private long dueAmountTotal;
}
