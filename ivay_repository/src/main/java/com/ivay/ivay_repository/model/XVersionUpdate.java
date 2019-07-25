package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * app版本更新信息
 * 
 * @author panshuhua
 */
@Data
@ApiModel("app版本更新")
public class XVersionUpdate {
    @ApiModelProperty("app版本号")
    private String versionNumber;
    @ApiModelProperty("app版本更新内容")
    private String versionContent;
    @ApiModelProperty("是否需要更新")
    private String needUpdate;
    @ApiModelProperty("app下载地址")
    private String appDownloadUrl;
}
