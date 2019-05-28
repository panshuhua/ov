package com.ivay.ivay_repository.model;

public class XUser extends XUserInfo{
	 private String userToken;   //用户token
	 private Integer status;      //状态
	 private Integer needverifyMapCode; //是否需要图形验证码
	 
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
