package com.ivay.ivay_repository.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName CollectionTaskResult
 * @Description 催收任务详情结果
 * @Author Ryan
 * @Date 2019/7/9 16:46
 */
@Data
public class CollectionTaskResult {

    @ApiModelProperty("催收任务id")
    private Integer id;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("还款状态")
    private Byte repaymentStatus;

    @ApiModelProperty("逾期级别")
    private String overdueLevel;

    @ApiModelProperty("逾期天数")
    private Integer overdueDay;

    @ApiModelProperty("当前应还")
    private Long amount;

    @JSONField(format = "yyyy-MM-dd")
    @ApiModelProperty("应还日期")
    private Date dueTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("指派时间")
    private Date assignTime;

    @ApiModelProperty("催收人")
    private String username;

    @ApiModelProperty("催收人id")
    private String userId;

    @ApiModelProperty("用戶gid")
    private String userGid;

    @ApiModelProperty("催收还款状态")
    private Byte collectionRepayStatus;


}
