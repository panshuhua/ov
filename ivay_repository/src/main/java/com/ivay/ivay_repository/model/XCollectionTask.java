package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("催收任务表")
public class XCollectionTask {
    @ApiModelProperty(value = "借款订单号")
    private String orderId;

    @ApiModelProperty(value = "还款gid")
    private String repaymentGid;

    @ApiModelProperty(value = "催收员id")
    private Integer collectorId;

    @ApiModelProperty(value = "催收状态：0等待指派, 1已指派 2正在催收 3催收完成  4催收失败")
    private String collectionStatus;

    @ApiModelProperty(value = "应追回的本金, 不含逾期利息")
    private String dueCollectionAmount;

    @ApiModelProperty(value = "追回本金")
    private String collectionAmount;

    @ApiModelProperty(value = "追回逾期利息")
    private String collectionOverdueFee;

    @ApiModelProperty(value = "有效标志位")
    private String enableFlag;

    private Long id;
    private Date createTime;
    private Date updateTime;
}
