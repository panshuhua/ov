package com.ivay.ivay_repository.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BlackUser {
    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "身份证")
    private String identityCard;

    private Long id;
}
