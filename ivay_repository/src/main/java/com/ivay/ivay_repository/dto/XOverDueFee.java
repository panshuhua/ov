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

    @ApiModelProperty("借款gid")
    private String gid;

    @ApiModelProperty("到期时间")
    private Date dueTime;

    @ApiModelProperty("逾期天数")
    private Integer dueDate;

    @ApiModelProperty("逾期一天的费用/逾期费用")
    private long overdueFee;

    @ApiModelProperty("逾期利息")
    private long overdueInterest;

    @ApiModelProperty("借款时间")
    private long loanPeriod;

    @ApiModelProperty("借款利率")
    private BigDecimal loanRate;

    @ApiModelProperty("用户gid")
    private String userGid;
}
