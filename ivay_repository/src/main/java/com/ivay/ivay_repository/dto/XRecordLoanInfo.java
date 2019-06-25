package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class XRecordLoanInfo {
    @ApiModelProperty("订单ID")
    private String orderId;
    @ApiModelProperty("用户gid")
    private String userGid;
    @ApiModelProperty("用户名")
    private String name;
    @ApiModelProperty("剩余需要还的逾期费用")
    private Long overdueFee;
    @ApiModelProperty("剩余需要还的逾期利息（不包含正常计息部分）")
    private Long overdueInterest;
    @ApiModelProperty("剩余本金")
    private Long dueAmount;
}
