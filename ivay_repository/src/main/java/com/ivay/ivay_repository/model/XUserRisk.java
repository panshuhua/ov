package com.ivay.ivay_repository.model;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class XUserRisk {
    @ApiModelProperty("用户gid")
    private String userGid;
    @ApiModelProperty("经度")
    private BigDecimal longitude;
    @ApiModelProperty("纬度")
    private BigDecimal latitude;

    @ApiModelProperty("设备imei")
    public String imei;
    @ApiModelProperty("国际移动用户识别码")
    public String imsi;
    @ApiModelProperty("手机号码")
    public String phoneNumber;

    public String carrierName;
    @ApiModelProperty("手机型号")
    public String phoneModel;
    @ApiModelProperty("用户唯一识别 id（userId, 上报极光所用的别名）")
    public String uid;
    @ApiModelProperty("wifimac地址")
    public String wifiMacAddress;
    @ApiModelProperty("系统版本")
    public String systemVersion;
    @ApiModelProperty("设备ipV4值")
    public String ipv4Address;

    private Date createTime;
    private Date updateTime;

    public String macAddress;
    public String phoneBrand;
    public String trafficWay;
}
