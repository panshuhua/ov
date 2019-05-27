package com.ivay.ivay_manage.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("文件实体")
public class XFileInfo implements Serializable {
    private static final long serialVersionUID = -5761547882766615438L;

    @ApiModelProperty("文件类型")
    private String contentType;

    @ApiModelProperty("文件大小")
    private long size;

    @ApiModelProperty("文件路径")
    private String path;

    @ApiModelProperty("文件相对路径")
    private String url;

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("文件md5")
    private String id;

    private Date createTime;
    private Date updateTime;
}
