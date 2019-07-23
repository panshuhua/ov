package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 调用VTP接口返回值
 * 
 * @author panshuhua
 * @date 2019/07/22
 */
public class VTPUpdateTranStatusResponse {
    @JsonProperty("CODE")
    private Integer CODE;
    @JsonProperty("MESSAGE")
    private String MESSAGE;
    @JsonProperty("SIGNED")
    private String SIGNED;

    @JsonIgnore
    public Integer getCODE() {
        return CODE;
    }

    public void setCODE(Integer cODE) {
        CODE = cODE;
    }

    @JsonIgnore
    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        MESSAGE = mESSAGE;
    }

    @JsonIgnore
    public String getSIGNED() {
        return SIGNED;
    }

    public void setSIGNED(String sIGNED) {
        SIGNED = sIGNED;
    }

}
