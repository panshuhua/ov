package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("用户扩展信息实体")
public class XUserExtInfo {
    @ApiModelProperty("用户gid")
    private String userGid;

    @ApiModelProperty("主要联系人")
    private String majorRelationship;

    @ApiModelProperty("主要联系人姓名")
    private String majorName;

    @ApiModelProperty("主要联系人电话")
    private String majorPhone;

    @ApiModelProperty("备用联系人")
    private String bakRelationship;

    @ApiModelProperty("备用联系人姓名")
    private String bakName;

    @ApiModelProperty("备用联系人电话")
    private String bakPhone;

    @ApiModelProperty("正面照")
    private String photo1Url;

    @ApiModelProperty("反面照")
    private String photo2Url;

    @ApiModelProperty("手持照")
    private String photo3Url;

    private long id;
    private Date createTime;
    private Date updateTime;
    private String enableFlag;
}
