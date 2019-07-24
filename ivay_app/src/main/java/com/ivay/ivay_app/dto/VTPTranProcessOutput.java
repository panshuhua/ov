package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * VTP输出参数
 * 
 * @author panshuhua
 * @date 2019/07/22
 */
public class VTPTranProcessOutput {
    @JsonProperty("SIGNED")
    private String SIGNED;
    @JsonProperty("DATA")
    private VTPTranOutputData DATA;

    @JsonIgnore
    public String getSIGNED() {
        return SIGNED;
    }

    public void setSIGNED(String sIGNED) {
        SIGNED = sIGNED;
    }

    @JsonIgnore
    public VTPTranOutputData getDATA() {
        return DATA;
    }

    public void setDATA(VTPTranOutputData dATA) {
        DATA = dATA;
    }

}
