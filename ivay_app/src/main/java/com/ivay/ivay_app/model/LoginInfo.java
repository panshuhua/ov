package com.ivay.ivay_app.model;

import com.ivay.ivay_app.annotation.Encryption;
import com.ivay.ivay_app.valid.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@ApiModel("注册登录参数实体")
@Encryption
public class LoginInfo {
	@NotEmpty(groups = {Update.class}, message = "validated.mobile.empty")
	@ApiModelProperty("用户手机号")
	private String mobile;
	
	@ApiModelProperty("用户登录密码")
	private String password;
	
	@ApiModelProperty("手机验证的token(只针对内部渠道app登录)")
	private String codeToken;
	
	@ApiModelProperty("手机短信验证码(更换设备时需要验证码)")
	private String verifyCode;
	
	@ApiModelProperty("手机验证token")
	private String verifyToken;
	
	@ApiModelProperty("是否老用户新设备登录")
	private String needCheckVerify;
	
	@ApiModelProperty("设备id")
	private String macCode;
	
	@ApiModelProperty("图形验证码")
	private String verifyMapCode;
	
	@ApiModelProperty("是否是短信验证码登录")
	private String isVerifyCodeLogin;
	
	@ApiModelProperty("经度")
	private BigDecimal longitude;
	
	@ApiModelProperty("纬度")
	private BigDecimal latitude;
	
}
