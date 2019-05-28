package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 注册/登录成功返回的对象
 * @author psh
 */
@Data
@ApiModel("注册登录成功返回的用户实体")
public class ReturnUser {
   @ApiModelProperty("用户状态")
   private String userStatus;
   @ApiModelProperty("用户gid")
   private String userGid;
   @ApiModelProperty("用户token")
   private String userToken;
   @ApiModelProperty("账号状态")
   private String accountStatus;
   @ApiModelProperty("是否需要图形验证码")
   private Integer needverifyMapCode;
   @ApiModelProperty("姓名")
   private String name;
   @ApiModelProperty("性别")
   private String sex;
   @ApiModelProperty("可用额度")
   private long canborrowAmount;
   @ApiModelProperty("授信额度")
   private long creditLine;
   @ApiModelProperty("手机号码")
   private String mobile;
   
}
