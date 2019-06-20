package com.ivay.ivay_repository.model;

import com.ivay.ivay_common.annotation.Decrypt;
import com.ivay.ivay_common.annotation.Encryption;
import com.ivay.ivay_common.valid.IdentityCard;
import com.ivay.ivay_common.valid.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("用户信息实体")
@Encryption // 加解密需要给请求体增加注解
public class XUserInfo {
    @ApiModelProperty("用户gid")
    private String userGid;

    @ApiModelProperty("电话号码")
    @Decrypt  // 需要解密的字段
    private String phone;

    @NotEmpty(groups = {Update.class}, message = "validated.name.empty")
    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("身份证")
    @IdentityCard(groups = {Update.class})
    @Decrypt  // 需要解密的字段
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
    @Decrypt  // 需要解密的字段
    private String password;

    @ApiModelProperty("授信额度")
    private Long creditLine;

    @ApiModelProperty("提額次數")
    private Integer creditLineCount;

    @ApiModelProperty("可用额度")
    private Long canborrowAmount;

    @ApiModelProperty("交易密码")
    @Decrypt  // 需要解密的字段
    private String transPwd;

    @ApiModelProperty("设备id")
    private String macCode;

    @ApiModelProperty("app消息推送token")
    private String fmcToken;
    
    @ApiModelProperty("经度")
    private String longitude;
    
    @ApiModelProperty("纬度")
    private String latitude;
    
    @ApiModelProperty("审核拒绝原因")
    private String refuseReason;

    @ApiModelProperty("审核类型：0人工审核 1 自动审核")
    private String refuseType;

    @ApiModelProperty("审核时间")
    private Date auditTime;

    private long id;
    private Date createTime;
    private Date updateTime;
    private String enableFlag;
}
