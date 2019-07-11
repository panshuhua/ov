package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("借款实体")
public class XOverdueOrderInfo {
    @ApiModelProperty("借款订单")
    private String orderId;

    @ApiModelProperty("还款状态")
    private String repaymentStatus;

    @ApiModelProperty("逾期级别")
    private int overdueLevel;

    @ApiModelProperty("借款金额")
    private Long loanAmount;

    @ApiModelProperty("应还日期")
    private Date dueTime;

    @ApiModelProperty("实还日期")
    private Date lastRepaymentTime;
}
