package com.ivay.ivay_app.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class XAuditCondition {

    @ApiModelProperty("用户gid")
    private String userGid;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("申请时间")
    private Date createTime;

    @ApiModelProperty("起始时间")
    private Date fromTime;

    @ApiModelProperty("截至时间")
    private Date toTime;

    @ApiModelProperty("审核状态")
    private String auditStatus;
}
