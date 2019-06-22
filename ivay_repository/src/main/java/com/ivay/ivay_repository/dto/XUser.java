package com.ivay.ivay_repository.dto;

import com.ivay.ivay_repository.model.XUserInfo;
import io.swagger.annotations.ApiModelProperty;

public class XUser extends XUserInfo {
    @ApiModelProperty("用户token")
    private String userToken;

    @ApiModelProperty("状态")
    private Integer status;      //状态

    @ApiModelProperty("是否需要图形验证码")
    private Integer needverifyMapCode;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public interface Status {
        int DISABLED = 0;
        int VALID = 1;
        int LOCKED = 2;
    }


    public Integer getNeedverifyMapCode() {
        return needverifyMapCode;
    }

    public void setNeedverifyMapCode(Integer needverifyMapCode) {
        this.needverifyMapCode = needverifyMapCode;
    }
}
