package com.ivay.ivay_repository.model;

public class SysLogs extends BaseEntity<Long> {

	private static final long serialVersionUID = -7809315432127036583L;
	private String userGid;
	private String phone;
	private String module;
	private Boolean flag;
	private String remark;
	private String requestId;
	private String code;

	public String getUserGid() {
		return userGid;
	}

	public void setUserGid(String userGid) {
		this.userGid = userGid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	

}
