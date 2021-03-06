package com.ivay.ivay_common.enums;

import io.swagger.models.auth.In;

/**
 * @EnumName CollectionStatusEnum
 * @Description 催收指派状态枚举
 * @Author Ryan
 * @Date 2019/7/9 13:57
 */
public enum CollectionStatusEnum {

    /**
     * 對應描述字段功能
     */
    WAITING_COLLECTION((byte)0,"等待指派"),
    ASSIGNED_COLLECTION((byte)1,"已指派"),
    DOING_COLLECTION((byte)2,"正在催收"),
    FINISH_COLLECTION((byte)3,"催收完成"),
    FAILED_COLLECTION((byte)4,"催收失败");

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

    private CollectionStatusEnum(byte status, String statusDesc){
        this.status = status;
        this.statusDesc = statusDesc;
    }

}
