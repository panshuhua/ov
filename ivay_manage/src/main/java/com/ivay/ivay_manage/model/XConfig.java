package com.ivay.ivay_manage.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("配置实体")
public class XConfig {
    @ApiModelProperty("配置项")
    private String type;

    @ApiModelProperty("配置内容")
    private String content;

    @ApiModelProperty("描述")
    private String description;

    private long id;
    private Date createTime;
    private Date updateTime;
}
