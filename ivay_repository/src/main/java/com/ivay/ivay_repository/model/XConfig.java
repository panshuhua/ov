package com.ivay.ivay_repository.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("配置实体")
public class XConfig {
    @ApiModelProperty("配置项")
    private String type;

    @ApiModelProperty("配置内容")
    private String content;

    @ApiModelProperty("语言值")
    private String lang;

    @ApiModelProperty("描述")
    private String description;

    private Long id;
}
