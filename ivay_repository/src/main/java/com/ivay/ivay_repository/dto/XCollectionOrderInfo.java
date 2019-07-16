package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("催收订单详情")
public class XCollectionOrderInfo {
    @ApiModelProperty("用户gid")
    private String userGid;

    @ApiModelProperty("借款gid")
    private String gid;

    @ApiModelProperty("借款订单号")
    private String orderId;

    @ApiModelProperty("借款时间")
    private Date loanTime;

    @ApiModelProperty("借款金额")
    private Long loanAmount;

    @ApiModelProperty("实际到账金额")
    private Long netAmount;

    @ApiModelProperty("剩余需要还的逾期费用")
    private Long overdueFee;

    @ApiModelProperty("借款状态")
    private Integer loanStatus;

    @ApiModelProperty("借款应还时间")
    private Date dueTime;

    @ApiModelProperty("借款剩余应还本金")
    private Long dueAmount;

    @ApiModelProperty("最后一次还款时间")
    private Date lastRepaymentTime;

    @ApiModelProperty("还款狀態")
    private Integer repaymentStatus;

    @ApiModelProperty("借款备注")
    private String memo;

    @ApiModelProperty("逾期天数")
    private int overdueDay;

    @ApiModelProperty("逾期级别")
    private String overdueLevel;
}
