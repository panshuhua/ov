package com.ivay.ivay_app.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端错误信息
 * 
 * @author panshuhua
 * @date 2019/07/16
 */
@Data
public class ErrorMsg {
    @ApiModelProperty("错误名称")
    private String errorName;
    @ApiModelProperty("错误信息")
    private String errorInfo;
}
