package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class XCollectionCalculate {
	private Long id;

	@ApiModelProperty("统计日期")
	private Date calculateDate;

	@ApiModelProperty("逾期账单（件）")
	private Integer overdueOrder;

	@ApiModelProperty("逾期用户（人）")
	private Integer overdueUser;

	@ApiModelProperty("逾期本金（盾）")
	private String overduePrincipal;

	@ApiModelProperty("应收总额（盾）")
	private String amountReceivable;

	@ApiModelProperty("还款用户（人）")
	private Integer numberRepay;

	@ApiModelProperty("收回金额（盾）")
	private String amountRepay;

	@ApiModelProperty("创建时间")
	private Date createTime;

	@ApiModelProperty("更新时间")
	private Date updateTime;


}
