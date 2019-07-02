package com.ivay.ivay_repository.model;

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

    @ApiModelProperty("追回额度")
    private String collectionAmount;

    @ApiModelProperty("有效标志位")
    private String enableFlag;

    private Long id;
    private Date createTime;
    private Date updateTime;
}
