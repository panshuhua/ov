<<<<<<< HEAD
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
   @ApiModelProperty("mac地址")
   private String macAddress;
   @ApiModelProperty("手机品牌")
   private String phoneBrand;
   @ApiModelProperty("手机流量类型")
   private String trafficWay;
   
   private Date createTime;
   private Date updateTime;
}
=======
package com.ivay.ivay_repository.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class XUserRisk {
   private String userGid;
   private String macCode;
   private BigDecimal longitude;
   private BigDecimal latitude;
   private String macAddress;
   private String phoneBrand;
   private String trafficWay;
}
>>>>>>> branch 'master' of https://gitee.com/loveanime/ivay_integration.git
