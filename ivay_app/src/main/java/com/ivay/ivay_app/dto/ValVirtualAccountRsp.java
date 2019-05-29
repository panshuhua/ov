package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ValVirtualAccountRsp implements Serializable {

	private static final long serialVersionUID = 35435494737580569L;

	@JsonProperty("ResponseCode")
	private String ResponseCode ;
	@JsonProperty("ResponseMessage")
	private String ResponseMessage ;
	@JsonProperty("RequestId")
	private String RequestId;
	@JsonProperty("AccName")
	private String AccName;
	@JsonProperty("AccNo")
	private String AccNo;
	@JsonProperty("ClientIdNo")
	private String ClientIdNo;
	@JsonProperty("IssuedDate")
	private String IssuedDate;
	@JsonProperty("IssuedPlace")
	private String IssuedPlace;
	@JsonProperty("CollectAmount")
	private String CollectAmount;
	@JsonProperty("ExpireDate")
	private String ExpireDate;
	@JsonProperty("AccountType")
	private String AccountType;
	@JsonProperty("OrderId")
	private String OrderId;
	@JsonProperty("Signature")
	private String Signature;
	
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
	@JsonIgnore
	public String getRequestId() {
		return RequestId;
	}
	public void setRequestId(String requestId) {
		RequestId = requestId;
	}
	@JsonIgnore
	public String getAccName() {
		return AccName;
	}
	public void setAccName(String accName) {
		AccName = accName;
	}
	@JsonIgnore
	public String getAccNo() {
		return AccNo;
	}
	public void setAccNo(String accNo) {
		AccNo = accNo;
	}
	@JsonIgnore
	public String getClientIdNo() {
		return ClientIdNo;
	}
	public void setClientIdNo(String clientIdNo) {
		ClientIdNo = clientIdNo;
	}
	@JsonIgnore
	public String getIssuedDate() {
		return IssuedDate;
	}
	public void setIssuedDate(String issuedDate) {
		IssuedDate = issuedDate;
	}
	@JsonIgnore
	public String getIssuedPlace() {
		return IssuedPlace;
	}
	public void setIssuedPlace(String issuedPlace) {
		IssuedPlace = issuedPlace;
	}
	@JsonIgnore
	public String getCollectAmount() {
		return CollectAmount;
	}
	public void setCollectAmount(String collectAmount) {
		CollectAmount = collectAmount;
	}
	@JsonIgnore
	public String getExpireDate() {
		return ExpireDate;
	}
	public void setExpireDate(String expireDate) {
		ExpireDate = expireDate;
	}
	@JsonIgnore
	public String getAccountType() {
		return AccountType;
	}
	public void setAccountType(String accountType) {
		AccountType = accountType;
	}
	@JsonIgnore
	public String getOrderId() {
		return OrderId;
	}
	public void setOrderId(String orderId) {
		OrderId = orderId;
	}
	@JsonIgnore
	public String getSignature() {
		return Signature;
	}
	public void setSignature(String signature) {
		Signature = signature;
	}

}
