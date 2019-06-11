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
