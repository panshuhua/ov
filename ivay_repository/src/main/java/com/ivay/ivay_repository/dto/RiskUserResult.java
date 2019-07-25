package com.ivay.ivay_repository.dto;

import com.alibaba.druid.sql.visitor.functions.Char;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName RiskUserInfo
 * @Description 白名单列表返回信息
 * @Author Ryan
 * @Date 2019/7/23 9:14
 */
@Data
public class RiskUserResult implements Serializable {

    private static final long serialVersionUID = 4944145282822071832L;

    private Integer id;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("用户状态")
    private Integer userStatus;

    private Integer accountStatus;

    @ApiModelProperty("销售人id")
    private Integer userId;

    @ApiModelProperty("销售人")
    private String username;

    @ApiModelProperty("指派状态")
    private Integer assignStatus;

    @ApiModelProperty("导入时间")
    private Date importTime;

    @ApiModelProperty("指派时间")
    private Date assignTime;
}
