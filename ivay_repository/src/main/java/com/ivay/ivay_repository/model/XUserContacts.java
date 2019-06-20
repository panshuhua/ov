package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@ApiModel("用户手机联系人实体")
public class XUserContacts {
    @ApiModelProperty("联系人所属用户id")
    private String userGid;

    @ApiModelProperty("联系人姓名")
    private String contactName;

    @ApiModelProperty("上传时间")
    private String updateDate;

    @ApiModelProperty("电话号码")
    private String phoneNumber;

    private Long id;

    public XUserContacts(String userGid, String updateDate,String contactName, String phoneNumber) {
        this.userGid = userGid;
        this.updateDate = updateDate;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }
}
