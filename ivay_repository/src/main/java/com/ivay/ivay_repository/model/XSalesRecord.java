package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class XSalesRecord {
	private Integer id;

	@ApiModelProperty("白名单id")
	private Integer riskUserId;

	@ApiModelProperty("销售员id")
	private Integer salesmanId;

	@ApiModelProperty("内容")
	private String content;

	private Date createTime;

	private Date updateTime;


}
