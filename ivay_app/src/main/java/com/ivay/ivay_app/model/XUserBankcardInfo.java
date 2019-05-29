package com.ivay.ivay_app.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("银行卡实体")
public class XUserBankcardInfo {

	@ApiModelProperty("银行卡gid")
	private String bankcardGid;

	@ApiModelProperty("银行gid")
	private String bankGid;

	@ApiModelProperty("用户gid")
	private String userGid;

	@ApiModelProperty("用户名")
	private String cardUserName;

	@ApiModelProperty("银行卡类型")
	private String accType;

	@ApiModelProperty("银行卡号")
	private String cardNo;

	private String status;

	private long id;
	private Date createTime;
	private Date updateTime;
	private String enableFlag;
}
