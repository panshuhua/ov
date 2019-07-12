package com.ivay.ivay_common.enums;

/**
 * @EnumName CollectionRepayStatusEnum
 * @Description 催收还款状态枚举
 * @Author Ryan
 * @Date 2019/7/9 17:21
 */
public enum CollectionRepayStatusEnum {

    /**
     * 狀態對應描述字段
     */
    UNDER_REPAYING((byte)1, "还款中"),
    FINISHED_REPAY((byte)2, "已还款"),
    FAILED_REPAY((byte)3, "还款失败"),
    OVERDUE((byte)4, "逾期");

    private byte status;
    private String statusDesc;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    private CollectionRepayStatusEnum(byte status, String statusDesc){
        this.status = status;
        this.statusDesc = statusDesc;
    }



}
