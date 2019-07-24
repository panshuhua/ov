package com.ivay.ivay_app.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

/**
 * VTP的还款交易信息
 * 
 * @author panshuhua
 * @date 2019/07/19
 */
@ApiModel("VTP还款交易信息")
public class LIST_TRANS {
    @JsonProperty("TRANS_ID")
    private String TRANS_ID;
    @JsonProperty("AMOUNT")
    private Long AMOUNT;
    @JsonProperty("CONTENT")
    private String CONTENT;
    @JsonProperty("TYPE")
    private Integer TYPE;
    @JsonProperty("EXTENDS_INFO")
    private List<String> EXTENDS_INFO;

    @JsonIgnore
    public String getTRANS_ID() {
        return TRANS_ID;
    }

    public void setTRANS_ID(String tRANS_ID) {
        TRANS_ID = tRANS_ID;
    }

    @JsonIgnore
    public Long getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(Long aMOUNT) {
        AMOUNT = aMOUNT;
    }

    @JsonIgnore
    public String getCONTENT() {
        return CONTENT;
    }

    public void setCONTENT(String cONTENT) {
        CONTENT = cONTENT;
    }

    @JsonIgnore
    public Integer getTYPE() {
        return TYPE;
    }

    public void setTYPE(Integer tYPE) {
        TYPE = tYPE;
    }

    @JsonIgnore
    public List<String> getEXTENDS_INFO() {
        return EXTENDS_INFO;
    }

    public void setEXTENDS_INFO(List<String> eXTENDS_INFO) {
        EXTENDS_INFO = eXTENDS_INFO;
    }

}
