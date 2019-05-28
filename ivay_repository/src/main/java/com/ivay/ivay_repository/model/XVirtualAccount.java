package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("虚拟账号实体")
public class XVirtualAccount {
	@ApiModelProperty("Account number code has been successfully created")
	private String accNo;
	@ApiModelProperty("The name of Account holder (name of USER)")
	private String accName;
	@ApiModelProperty("The client id no of the Account holder(USER)")
	private String clientidNo;
	@ApiModelProperty("Date of issuing people's identity cards Format: YYYY-MM-DD")
	private String issuedDate;
	@ApiModelProperty("Place of issuing people's identity cards")
	private String issuedPlace;
	@ApiModelProperty("Amount must be collected")
	private Integer collectAmount;
	@ApiModelProperty("Expire date for collect money, format:YYYY-MM-DD")
	private String expireDate;
	@ApiModelProperty("Account with indentifier or without indentifier")
	private Integer accountType;
	@ApiModelProperty("An unique id code for account without indentifier")
	private String orderId;
	@ApiModelProperty("创建时间")
	private Date createTime;
	@ApiModelProperty("更新时间")
    private Date updateTime;
	@ApiModelProperty("是否有效")
    private String enableFlag;
	@ApiModelProperty("请求Id")
    private String requestId;
	@ApiModelProperty("响应编码")
    private String responseCode;
	@ApiModelProperty("响应信息")
    private String responseMessage;
}
