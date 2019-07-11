package com.ivay.ivay_common.enums;

/**
 * @EnumName RepaymentStatusEnum
 * @Description 借款记录状态枚举
 * @Author Ryan
 * @Date 2019/7/9 17:21
 */
public enum RepaymentStatusEnum {
//  `repayment_status` int(1) DEFAULT '0' COMMENT '还款状态：0待还款 1还款中 2已还款 3还款失败 4逾期',
    WAITING_REPAY(0, "待还款"),
    UNDER_REPAYING(1, "还款中"),
    FINISHED_REPAY(2, "已还款"),
    FAILED_REPAY(3, "还款失败"),
    OVERDUE(4, "逾期");

    private Integer status;
    private String statusDesc;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    private RepaymentStatusEnum(Integer status, String statusDesc){
        this.status = status;
        this.statusDesc = statusDesc;
    }



}
