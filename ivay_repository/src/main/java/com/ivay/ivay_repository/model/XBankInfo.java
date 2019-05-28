package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("银行实体")
public class XBankInfo {
	@ApiModelProperty("银行gid")
	private String bankGid;

	@ApiModelProperty("银行代码")
	private String bankNo;

	@ApiModelProperty("银行名称")
	private String bankName;

	@ApiModelProperty("是否账号")
	private String isAccount;

	@ApiModelProperty("是否卡")
	private String isCard;

	private long id;
	private Date createTime;
	private Date updateTime;
	private String enableFlag;
}
