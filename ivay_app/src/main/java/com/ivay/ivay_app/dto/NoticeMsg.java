package com.ivay.ivay_app.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 短信/消息推送的内容
 * 
 * @author panshuhua
 * @date 2019/07/04
 */
@Data
public class NoticeMsg {
    @ApiModelProperty("firebase发送需要的fmc_token")
    private String fmcToken;
    @ApiModelProperty("消息推送标题")
    private String title;
    @ApiModelProperty("消息推送内容")
    private String firebaseMsg;
    @ApiModelProperty("页面加密短链接")
    private String pageId;
    @ApiModelProperty("借款gid")
    private String gid;
    @ApiModelProperty("手机号码")
    private String phone;
    @ApiModelProperty("手机短信内容")
    private String phoneMsg;
    @ApiModelProperty("用户gid")
    private String userGid;
    @ApiModelProperty("短信链接中的key")
    private String key;
}
