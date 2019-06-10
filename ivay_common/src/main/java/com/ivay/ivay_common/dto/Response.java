package com.ivay.ivay_common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class Response<T> implements Serializable {
    @ApiModelProperty(value = "返回状态")
    private ResponseInfo status = new ResponseInfo("200", "OK");

    @ApiModelProperty(value = "返回对象")
    private T bo;

    public T getBo() {
        return bo;
    }

    public void setBo(T bo) {
        this.bo = bo;
    }

    public ResponseInfo getStatus() {
        return status;
    }

    public void setStatus(String code, String message) {
        this.status.setCode(code);
        this.status.setMessage(message);
    }

    public Response() {
    }

    public Response(String code, String message) {
        this.status.setCode(code);
        this.status.setMessage(message);
    }
}
