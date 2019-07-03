package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 对账结果
 * 
 * @author panshuhua
 * @date 2019/07/02
 */
@Data
public class AccountCheckResult {
    @ApiModelProperty("baokim中的数目")
    private Integer baokimCount;
    @ApiModelProperty("ovay中的数目")
    private Integer ovayCount;
    @ApiModelProperty("baokim中的总数")
    private Long baokimAmount;
    @ApiModelProperty("ovay中的总数")
    private Long ovayAmount;
    @ApiModelProperty("比对的账目类型")
    private String type;
    @ApiModelProperty("合作伙伴")
    private String partner;
    @ApiModelProperty("统计日期")
    private String time;
}
