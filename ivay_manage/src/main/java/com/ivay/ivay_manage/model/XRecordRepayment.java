package com.ivay.ivay_manage.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("还款实体")
public class XRecordRepayment {
    @ApiModelProperty("还款记录gid")
    private String gid;

    @ApiModelProperty("用户gid")
    private String userGid;

    @ApiModelProperty("借款记录gid")
    private String recordLoanGid;

    @ApiModelProperty("还款类型")
    private Integer repaymentType;

    @ApiModelProperty("还款方式")
    private Integer repaymentWay;

    @ApiModelProperty("本次还款金额")
    private long repaymentAmount;

    @ApiModelProperty("还款状态")
    private String repaymentStatus;

    @ApiModelProperty("扣款结束时间")
    private Date endTime;

    @ApiModelProperty("还款失败原因")
    private String failReason;
    
    private long repaymentOverdueFee;
    
    private String orderId;

    private long id;
    private Date createTime;
    private Date updateTime;
}
