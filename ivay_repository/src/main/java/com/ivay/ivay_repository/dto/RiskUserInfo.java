package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName RiskUserInfo
 * @Description 白名单信息
 * @Author Ryan
 * @Date 2019/7/23 9:14
 */
@Data
public class RiskUserInfo {

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("用户状态")
    private Integer userStatus;

    @ApiModelProperty("销售人id")
    private Integer userId;

    @ApiModelProperty("销售人")
    private String username;

    @ApiModelProperty("指派状态")
    private Integer assignStatus;

    @ApiModelProperty("处理状态")
    private Integer dealStatus;

    @ApiModelProperty("导入时间开始")
    private Date importTimeStart;

    @ApiModelProperty("导入时间结束")
    private Date importTimeEnd;

    @ApiModelProperty("指派时间开始")
    private Date assignTimeStart;

    @ApiModelProperty("指派时间结束")
    private Date assignTimeEnd;
}
