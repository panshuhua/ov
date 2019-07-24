package com.ivay.ivay_repository.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName CustomerInfo
 * @Description 客服系统列表搜索字段
 * @Author Ryan
 * @Date 2019/7/22 17:38
 */
@Data
@ApiModel("客服系统列表搜索字段")
public class CustomerInfo {

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("身份证号码")
    private String identityCard;

    @ApiModelProperty("生日")
    private String birthday;

    @ApiModelProperty("语言切换")
    private String lang;
}
