package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class XAppEvent {
    @ApiModelProperty(value = "用户gid 或 借款gid")
    private String gid;

    @ApiModelProperty(value = "0 授信成功 1 借款成功")
    private String type;

    @ApiModelProperty(value = "是否已经上传")
    private String isUpload;

    @ApiModelProperty(value = "有效标志位")
    private String enableFlag;

    private Long id;
    private Date createTime;
    private Date updateTime;
}
