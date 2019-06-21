package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EbayBlanceFlucNoticeRsp {
	@JsonProperty("ResponseCode")
	private String ResponseCode ;
	@JsonProperty("ResponseMessage")
	private String ResponseMessage ;
	
	@JsonIgnore
	public String getResponseCode() {
		return ResponseCode;
	}
	public void setResponseCode(String responseCode) {
		ResponseCode = responseCode;
	}
	@JsonIgnore
	public String getResponseMessage() {
		return ResponseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		ResponseMessage = responseMessage;
	}
	
	
}
