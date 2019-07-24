package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("审计员可审计用户实体")
public class XAuditUser {
    @ApiModelProperty("审计员id")
    private String sysUserId;
    @ApiModelProperty("待审计用户gid")
    private String userGid;

    private long id;
    private Date createTime;
    private Date updateTime;
}
