package com.ivay.ivay_manage.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class XAuditDetail {
    @ApiModelProperty("用户gid")
    private String userGid;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("身份证")
    private String identityCard;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("生日")
    private Date birthday;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("正面照")
    private String photo1Url;

    @ApiModelProperty("反面照")
    private String photo2Url;

    @ApiModelProperty("手持照")
    private String photo3Url;
}
