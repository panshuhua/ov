package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("借款实体")
public class XRecordLoan {
    @ApiModelProperty("借款gid")
    private String gid;

    @ApiModelProperty("借款订单号")
    private String orderId;

    @ApiModelProperty("用户gid")
    private String userGid;

    @ApiModelProperty("银行卡gid")
    private String bankcardGid;

    @ApiModelProperty("产品id")
    private Integer productId;

    @ApiModelProperty("借款天数")
    private Integer loanPeriod;

    @ApiModelProperty("借款时间")
    private Date loanTime;

    @ApiModelProperty("借款数目")
    private Long loanAmount;

    @ApiModelProperty("借款利率")
    private BigDecimal loanRate;

    @ApiModelProperty("借款服务费")
    private Long fee;

    @ApiModelProperty("借款利息")
    private Long interest;

    @ApiModelProperty("实际到账金额")
    private Long netAmount;

    @ApiModelProperty("剩余需要还的逾期费用")
    private Long overdueFee;

    @ApiModelProperty("总逾期服务费")
    private Long overdueFeeTotal;

    @ApiModelProperty("应还逾期利息，暂时不用恒为0")
    private Long overdueInterest;

    @ApiModelProperty("用于记录多还的金额")
    private Long moreRepaymentAmount;

    @ApiModelProperty("借款状态")
    private Integer loanStatus;

    @ApiModelProperty("借款失败原因")
    private String failReason;

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

    private long id;
    private Date createTime;
    private Date updateTime;
}
