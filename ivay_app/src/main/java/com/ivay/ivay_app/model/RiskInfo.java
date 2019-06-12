package com.ivay.ivay_app.model;

import com.ivay.ivay_repository.model.XUserContacts;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

/**
 * 风控需要审核的信息
 *
 * @author psh
 */
@Data
public class RiskInfo {
    @ApiModelProperty("用户gid")
    private String userGid;
    @ApiModelProperty("联系人列表")
    private Set<XUserContacts> contacts;
    @ApiModelProperty("经度")
    private BigDecimal longitude;
    @ApiModelProperty("纬度")
    private BigDecimal latitude;
    @ApiModelProperty("社交类app的个数")
    private Integer appNum;
    @ApiModelProperty("风控数据类型")
    private String type;
    @ApiModelProperty("mac地址")
    private String macAddress;
    @ApiModelProperty("手机品牌")
    private String phoneBrand;
    @ApiModelProperty("手机流量类型")
    private String trafficWay;
}
