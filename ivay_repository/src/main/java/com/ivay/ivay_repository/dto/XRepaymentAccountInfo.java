package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("逾期还款信息")
public class XRepaymentAccountInfo {
    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("订单号")
    private String orderId;

    @ApiModelProperty("借款日期")
    private String loanTime;

    @ApiModelProperty("到期日期")
    private String dueTime;

    @ApiModelProperty("借款金额")
    private long loanAmount;

    @ApiModelProperty("应还金额")
    private long dueAmountTotal;

    @ApiModelProperty("虚拟账号")
    private String accNo;

    @ApiModelProperty("账号名")
    private String accName;
}
