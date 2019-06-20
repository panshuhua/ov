package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("借款利率实体")
public class XLoanRate {
    @ApiModelProperty(value = "用户gid", name = "userGid")
    private String userGid;
    @ApiModelProperty(value = "手续费率", name = "feeRate")
    private BigDecimal feeRate;
    @ApiModelProperty(value = "利息率", name = "interestRate")
    private BigDecimal interestRate;
    @ApiModelProperty(value = "借款周期", name = "period")
    private Integer period;

    private Long id;
    private Date createTime;
    private Date updateTime;
    private String enableFlag;
}
