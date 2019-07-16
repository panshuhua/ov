package com.ivay.ivay_app.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MsgVerifyCode {
    @ApiModelProperty("手机号码")
    private String mobile;
    @ApiModelProperty("短信验证码")
    private String authCode;
    @ApiModelProperty("手机短信")
    private String phoneMsg;
    @ApiModelProperty("发送方式")
    private String sendMethod; // 发送方式
}
