package com.ivay.ivay_repository.dto;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class XOverDueFee {
    @ApiModelProperty("剩余本金")
    private Long dueAmount;

    @ApiModelProperty("firebase发送的fmcToken")
    private String fmcToken;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("到期时间")
    private Date dueTime;

    @ApiModelProperty("逾期天数")
    private Integer dueDate;

    @ApiModelProperty("逾期一天的费用")
    private long overdueFee;

    @ApiModelProperty("借款时间")
    private long loanPeriod;

    @ApiModelProperty("借款利率")
    private BigDecimal loanRate;
}
