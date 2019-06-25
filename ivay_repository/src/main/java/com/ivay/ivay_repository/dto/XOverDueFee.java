package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class XOverDueFee {
    private Long dueAmount;
    private String fmcToken;
    private String phone;
    private Date dueTime;

    @ApiModelProperty("逾期一天的费用")
    private long overdueFee;

    @ApiModelProperty("借款时间")
    private long loanPeriod;

    @ApiModelProperty("借款利率")
    private BigDecimal loanRate;
}
