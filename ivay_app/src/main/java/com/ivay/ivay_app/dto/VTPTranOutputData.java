package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * VTP输出参数交易数据
 * 
 * @author panshuhua
 * @date 2019/07/22
 */
public class VTPTranOutputData {
    @JsonProperty("TRANS_ID")
    private String TRANS_ID;
    @JsonProperty("REF_ID")
    private String REF_ID;
    @JsonProperty("TRANS_STATUS")
    private Integer TRANS_STATUS;
    @JsonProperty("TRANS_DATE")
    private Long TRANS_DATE;
    @JsonProperty("TRANS_MSG")
    private String TRANS_MSG;

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
    public Integer getTRANS_STATUS() {
        return TRANS_STATUS;
    }

    public void setTRANS_STATUS(Integer tRANS_STATUS) {
        TRANS_STATUS = tRANS_STATUS;
    }

    @JsonIgnore
    public Long getTRANS_DATE() {
        return TRANS_DATE;
    }

    public void setTRANS_DATE(Long tRANS_DATE) {
        TRANS_DATE = tRANS_DATE;
    }

    @JsonIgnore
    public String getTRANS_MSG() {
        return TRANS_MSG;
    }

    public void setTRANS_MSG(String tRANS_MSG) {
        TRANS_MSG = tRANS_MSG;
    }

}
