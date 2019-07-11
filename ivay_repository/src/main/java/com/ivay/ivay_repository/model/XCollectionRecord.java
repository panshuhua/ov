package com.ivay.ivay_repository.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("催收记录表")
public class XCollectionRecord {
    @ApiModelProperty("催收任务id")
    private Integer taskId;

    @ApiModelProperty("催收员id")
    private Integer collectorId;

    @ApiModelProperty("借款订单id")
    private String orderId;

    @ApiModelProperty("催收员姓名")
    private String collectorName;

    @ApiModelProperty("催收电话")
    private String collectionPhone;

    @ApiModelProperty("催收状态")
    private String collectionReason;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("催收时间")
    private Date collectionTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("有效标志位")
    private String enableFlag;

    private Integer id;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private Date updateTime;
}