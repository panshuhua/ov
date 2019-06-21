package com.ivay.ivay_repository.dto;

import com.ivay.ivay_repository.model.XUserBankcardInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("银行卡和银行信息")
public class XUserCardAndBankInfo extends XUserBankcardInfo {
    @ApiModelProperty("银行编号")
    private String bankNo;

    @ApiModelProperty("银行名")
    private String bankName;

}
