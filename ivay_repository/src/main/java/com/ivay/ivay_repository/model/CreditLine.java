package com.ivay.ivay_repository.model;

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

    @ApiModelProperty("提額次數")
    private Integer creditLineCount;

    @ApiModelProperty("可用额度")
    private Long canborrowAmount;

    @ApiModelProperty("授信状态")
    private String userStatus;
}
