package com.ivay.ivay_common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 短信链接内容-返回到前台
 * 
 * @author panshuhua
 * @date 2019/07/04
 */
@Data
public class MsgLinkData {
    @ApiModelProperty("页面加密短链接")
    private String to;
    @ApiModelProperty("借款gid")
    private String gid;
    @ApiModelProperty("用户gid")
    private String userGid;
}
