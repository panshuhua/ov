package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EbayTransfersRsp extends TransfersRsp{
	
	private static final long serialVersionUID = 2916065645780440646L;
	
	//sub-银行返回的失败信息
    @JsonProperty("Reason")
	private String Reason;
	@JsonProperty("SubResponseCode")
	private SubResponseCode SubResponseCode;
	
	//余额
	@JsonProperty("CurrentBalance")
	private String CurrentBalance;

	@JsonIgnore
	public String getReason() {
		return Reason;
	}

	public void setReason(String reason) {
		this.Reason = reason;
	}

	@JsonIgnore
	public SubResponseCode getSubResponseCode() {
		return SubResponseCode;
	}

	public void setSubResponseCode(SubResponseCode subResponseCode) {
		this.SubResponseCode = subResponseCode;
	}

	@JsonIgnore
	public String getCurrentBalance() {
		return CurrentBalance;
	}

	public void setCurrentBalance(String currentBalance) {
		CurrentBalance = currentBalance;
	}
	
	
}
