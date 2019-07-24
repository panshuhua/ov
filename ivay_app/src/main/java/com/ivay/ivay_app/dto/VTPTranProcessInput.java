package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * VTP交易过程传入参数
 * 
 * @author panshuhua
 * @date 2019/07/22
 */
public class VTPTranProcessInput {
    @JsonProperty("TRANS_ID")
    private String TRANS_ID;
    @JsonProperty("REF_ID")
    private String REF_ID;
    @JsonProperty("CUSTOMER_CODE")
    private String CUSTOMER_CODE;
    @JsonProperty("AMOUNT")
    private Long AMOUNT;
    @JsonProperty("FEE")
    private Long FEE;
    @JsonProperty("CONTENT")
    private String CONTENT;
    @JsonProperty("AGENT_USER")
    private String AGENT_USER;
    @JsonProperty("AGENT_NAME")
    private String AGENT_NAME;
    @JsonProperty("TRANS_DATE")
    private Long TRANS_DATE;

    @JsonIgnore
    public String getTRANS_ID() {
        return TRANS_ID;
    }

    public void setTRANS_ID(String tRANS_ID) {
        TRANS_ID = tRANS_ID;
    }

    @JsonIgnore
    public String getREF_ID() {
        return REF_ID;
    }

    public void setREF_ID(String rEF_ID) {
        REF_ID = rEF_ID;
    }

    @JsonIgnore
    public String getCUSTOMER_CODE() {
        return CUSTOMER_CODE;
    }

    public void setCUSTOMER_CODE(String cUSTOMER_CODE) {
        CUSTOMER_CODE = cUSTOMER_CODE;
    }

    @JsonIgnore
    public Long getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(Long aMOUNT) {
        AMOUNT = aMOUNT;
    }

    @JsonIgnore
    public Long getFEE() {
        return FEE;
    }

    public void setFEE(Long fEE) {
        FEE = fEE;
    }

    @JsonIgnore
    public String getCONTENT() {
        return CONTENT;
    }

    public void setCONTENT(String cONTENT) {
        CONTENT = cONTENT;
    }

    @JsonIgnore
    public String getAGENT_USER() {
        return AGENT_USER;
    }

    public void setAGENT_USER(String aGENT_USER) {
        AGENT_USER = aGENT_USER;
    }

    @JsonIgnore
    public String getAGENT_NAME() {
        return AGENT_NAME;
    }

    public void setAGENT_NAME(String aGENT_NAME) {
        AGENT_NAME = aGENT_NAME;
    }

    @JsonIgnore
    public Long getTRANS_DATE() {
        return TRANS_DATE;
    }

    public void setTRANS_DATE(Long tRANS_DATE) {
        TRANS_DATE = tRANS_DATE;
    }

}
