package com.ivay.ivay_repository.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class XCollectionCalculate {
	private Long id;

	@JSONField(format = "yyyy-MM-dd")
	@ApiModelProperty("统计日期")
	private Date calculateDate;

	@ApiModelProperty("逾期账单（件）")
	private Integer overdueOrder = 0;

	@ApiModelProperty("逾期用户（人）")
	private Integer overdueUser = 0;

	@ApiModelProperty("逾期本金（盾）")
	private Long overduePrincipal = 0L;

	@ApiModelProperty("应收总额（盾）")
	private Long amountReceivable = 0L;

	@ApiModelProperty("还款用户（人）")
	private Integer numberRepay = 0;

	@ApiModelProperty("收回金额（盾）")
	private Long amountRepay = 0L;

	@ApiModelProperty("创建时间")
	private Date createTime;

	@ApiModelProperty("更新时间")
	private Date updateTime;


}
