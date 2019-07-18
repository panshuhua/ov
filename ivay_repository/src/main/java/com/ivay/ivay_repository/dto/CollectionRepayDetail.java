package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName CollectionRepayDetail
 * @Description 催收还款名单实体类
 * @Author Ryan
 * @Date 2019/7/17 19:23
 */
@Data
public class CollectionRepayDetail implements Serializable {

    private static final long serialVersionUID = 743440417132199365L;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("应还时间")
    private Date dueTime;

    @ApiModelProperty("实还时间")
    private Date createTime;

    @ApiModelProperty("还款金额")
    private Long repaymentAmount;

}
