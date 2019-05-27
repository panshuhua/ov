package com.ivay.ivay_manage.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("用户信息实体")
public class XUserInfo {
    @ApiModelProperty("用户gid")
    private String userGid;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("身份证")
    private String identityCard;

    @ApiModelProperty("生日")
    private Date birthday;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("学历")
    private String education;

    @ApiModelProperty("婚姻状态")
    private String marital;

    @ApiModelProperty("地址")
    private String place;

    @ApiModelProperty("收入")
    private Long income;

    @ApiModelProperty("用户业务状态")
    private String userStatus;

    @ApiModelProperty("账号状态")
    private String accountStatus;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("授信额度")
    private Long creditLine;

    @ApiModelProperty("提額次數")
    private Integer creditLineCount;

    @ApiModelProperty("可用额度")
    private Long canborrowAmount;

    @ApiModelProperty("交易密码")
    private String transPwd;

    private long id;
    private Date createTime;
    private Date updateTime;
    private String enableFlag;
}
