package com.ivay.ivay_repository.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName CollectionTaskInfo
 * @Description 催收任务信息
 * @Author Ryan
 * @Date 2019/7/9 15:31
 */
@Data
public class CollectionTaskInfo {

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("身份证号")
    private String identityCard;

    @ApiModelProperty("逾期级别")
    private String overdueLevel;

    @ApiModelProperty("催收人")
    private String username;

    @ApiModelProperty("催收还款状态")
    private String collectionRepayStatus;

    @ApiModelProperty("指派状态")
    private String collectionStatus;

    @ApiModelProperty("應還時間開始")
    private Date payDateStart;

    @ApiModelProperty("應還時間結束")
    private Date payDateEnd;

    @ApiModelProperty("指派時間開始")
    private Date collectionDateStart;

    @ApiModelProperty("指派時間結束")
    private Date collectionDateEnd;

    @ApiModelProperty("還款時間開始")
    private Date repayTimeStart;

    @ApiModelProperty("還款時間結束")
    private Date repayTimeEnd;
}
