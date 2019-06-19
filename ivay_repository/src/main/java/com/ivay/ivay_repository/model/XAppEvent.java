package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class XAppEvent {
    @ApiModelProperty(value = "用户gid 或 借款订单id")
    private String gid;

    @ApiModelProperty(value = "0 授信 1 借款")
    private String type;

    @ApiModelProperty(value = "0失败 1成功")
    private String isSuccess;

    @ApiModelProperty(value = "有效标志位")
    private String enableFlag;

    private Long id;
    private Date createTime;
    private Date updateTime;
}
