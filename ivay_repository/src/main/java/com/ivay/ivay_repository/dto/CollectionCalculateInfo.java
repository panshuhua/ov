package com.ivay.ivay_repository.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * @ClassName CollectionCalculateInfo
 * @Description 统计报表请求参数
 * @Author Ryan
 * @Date 2019/7/17 14:50
 */
@Data
public class CollectionCalculateInfo{

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开始时间")
    private Date beginTime;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("结束时间")
    private Date endTime;
}
