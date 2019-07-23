package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

/**
 * VTP还款通道客户信息参数
 * 
 * @author panshuhua
 * @date 2019/07/19
 */
@ApiModel("VTP还款通道-客户信息参数")
public class CUSTOMER_INFO {
    @JsonProperty("CODE")
    private String CODE;
    @JsonProperty("PHONE")
    private String PHONE;
    @JsonProperty("DISPLAYNAME")
    private String DISPLAYNAME;
    @JsonProperty("EXTENDS_INFO")
    private String EXTENDS_INFO;

    @JsonIgnore
    public String getCODE() {
        return CODE;
    }

    public void setCODE(String cODE) {
        CODE = cODE;
    }

    @JsonIgnore
    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String pHONE) {
        PHONE = pHONE;
    }

    @JsonIgnore
    public String getDISPLAYNAME() {
        return DISPLAYNAME;
    }

    public void setDISPLAYNAME(String dISPLAYNAME) {
        DISPLAYNAME = dISPLAYNAME;
    }

    @JsonIgnore
    public String getEXTENDS_INFO() {
        return EXTENDS_INFO;
    }

    public void setEXTENDS_INFO(String eXTENDS_INFO) {
        EXTENDS_INFO = eXTENDS_INFO;
    }

}
