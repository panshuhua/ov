package com.ivay.ivay_common.dto;

import lombok.Data;

/**
 * fpt发送短信获取token
 * 
 * @author panshuhua
 * @date 2019/07/01
 */
@Data
public class FptAccessTokenReq {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String scope;
    private String session_id;
}
