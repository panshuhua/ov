package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发送短信验证码返回信息
 * @author psh
 */
@Data
@ApiModel("手机短信验证码实体")
public class VerifyCodeInfo {
   @ApiModelProperty("短信验证码发送状态")
   private String status;
   @ApiModelProperty("手机短信验证码")
   private String codeToken;
   @ApiModelProperty("短信验证码有效时间")
   private long effectiveTime;
}
