package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SalesRecordResult
 * @Description 电销记录
 * @Author Ryan
 * @Date 2019/7/24 9:16
 */
@Data
public class SalesRecordResult implements Serializable {

    private static final long serialVersionUID = 7813882799792139577L;

    private Integer id;

    @ApiModelProperty("销售员")
    private String username;

    @ApiModelProperty("内容")
    private String content;

    private Date createTime;

}
