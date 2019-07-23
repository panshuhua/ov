package com.ivay.ivay_app.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

/**
 * vtp总的交易信息
 * 
 * @author panshuhua
 * @date 2019/07/19
 */
@ApiModel("VTP总的交易信息")
public class VTPTranInfo {
    @JsonProperty("QUERY_CODE")
    private String QUERY_CODE;
    @JsonProperty("CUSTOMER_INFO")
    private CUSTOMER_INFO CUSTOMER_INFO;
    @JsonProperty("LIST_TRANS")
    private List<LIST_TRANS> LIST_TRANS;

    @JsonIgnore
    public String getQUERY_CODE() {
        return QUERY_CODE;
    }

    public void setQUERY_CODE(String qUERY_CODE) {
        QUERY_CODE = qUERY_CODE;
    }

    @JsonIgnore
    public CUSTOMER_INFO getCUSTOMER_INFO() {
        return CUSTOMER_INFO;
    }

    public void setCUSTOMER_INFO(CUSTOMER_INFO cUSTOMER_INFO) {
        CUSTOMER_INFO = cUSTOMER_INFO;
    }

    @JsonIgnore
    public List<LIST_TRANS> getLIST_TRANS() {
        return LIST_TRANS;
    }

    public void setLIST_TRANS(List<LIST_TRANS> lIST_TRANS) {
        LIST_TRANS = lIST_TRANS;
    }

}
