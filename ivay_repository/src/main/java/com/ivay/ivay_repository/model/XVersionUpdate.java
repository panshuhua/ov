package com.ivay.ivay_repository.model;

import lombok.Data;

/**
 * app版本更新信息
 * @author panshuhua
 */
@Data
public class XVersionUpdate {
   private String userGid;
   private String versionNumber;
   private String versionContent;
   private String needUpdate;
}
