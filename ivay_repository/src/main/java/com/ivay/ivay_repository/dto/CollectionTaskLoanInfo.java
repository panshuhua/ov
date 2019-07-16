package com.ivay.ivay_repository.dto;

import com.ivay.ivay_repository.model.XCollectionTask;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName CollectionTaskLoanInfo
 * @Description 催单任务和订单对应信息
 * @Author Ryan
 * @Date 2019/7/15 17:12
 */
@Data
public class CollectionTaskLoanInfo extends XCollectionTask {

    private int loanStatus;

    private int repaymentStatus;

    private Date dueTime;

    private long dueAmount;

    private long overdueFee;
}
