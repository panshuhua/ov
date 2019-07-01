package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * fpt发送短信
 * 
 * @author panshuhua
 * @date 2019/07/01
 */
public class FptSendReq {
    private String access_token;
    private String session_id;
    @JsonProperty("BrandName")
    private String BrandName;
    @JsonProperty("Phone")
    private String Phone;
    @JsonProperty("Message")
    private String Message;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    @JsonIgnore
    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    @JsonIgnore
    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    @JsonIgnore
    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

}
