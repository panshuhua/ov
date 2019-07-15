package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 授信额度
 *
 * @author psh
 */
@Data
@ApiModel("授信额度返回实体")
public class CreditLine {
    @ApiModelProperty("用户gid")
    private String userGid;

    @ApiModelProperty("授信额度")
    private Long creditLine;

    @ApiModelProperty("可用额度")
    private Long canborrowAmount;

    @ApiModelProperty("授信状态")
    private String userStatus;

    @ApiModelProperty("逾期笔数")
    private int overdueCount;

    @ApiModelProperty("拒绝类型")
    private String refuseType;

    @ApiModelProperty("拒绝理由")
    private String refuseReason;
}
