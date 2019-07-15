package com.ivay.ivay_app.dto;

import lombok.Data;

@Data
public class MsgVerifyCode {
    private String mobile;
    private String authCode;
    private String phoneMsg;
    private String sendMethod; // 发送方式
}
