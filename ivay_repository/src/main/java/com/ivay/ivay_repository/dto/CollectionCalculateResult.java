package com.ivay.ivay_repository.dto;

import com.ivay.ivay_repository.model.XCollectionCalculate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName CollectionCalculateResult
 * @Description 统计返回结果
 * @Author Ryan
 * @Date 2019/7/17 14:59
 */
@Data
public class CollectionCalculateResult implements Serializable {

    private static final long serialVersionUID = -1328194401251215633L;

    @ApiModelProperty("统计列表")
    private List<XCollectionCalculate> xCollectionCalculateList;

    @ApiModelProperty("逾期总账单")
    private int totalOverdueOrder;

    @ApiModelProperty("逾期用户")
    private int totalOverdueUser;

    @ApiModelProperty("逾期本金")
    private long totalOverdueMoney;

    @ApiModelProperty("应收总额")
    private long totalReceivable;

    @ApiModelProperty("还款用户")
    private int totalRepayUser;

    @ApiModelProperty("回收总额")
    private int totalRepayMoney;
}
