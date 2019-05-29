package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CollectionTransactionNotice {
   @JsonProperty("RequestId") 
   private String RequestId;
   @JsonProperty("RequestTime")
   private String RequestTime;
   @JsonProperty("PartnerCode")
   private String PartnerCode;
   @JsonProperty("AccNo")
   private String AccNo;
   @JsonProperty("ClientIdNo")
   private String ClientIdNo;
   @JsonProperty("TransId")
   private String TransId;
   @JsonProperty("TransAmount")
   private String TransAmount;
   @JsonProperty("TransTime")
   private String TransTime;
   @JsonProperty("BefTransDebt")
   private String BefTransDebt;
   @JsonProperty("AffTransDebt")
   private String AffTransDebt;
   @JsonProperty("AccountType")
   private String AccountType;
   @JsonProperty("OrderId")
   private String OrderId;
   @JsonProperty("Signature")
   private String Signature;

@JsonIgnore
public String getRequestId() {
	return RequestId;
}
public void setRequestId(String requestId) {
	RequestId = requestId;
}
@JsonIgnore
public String getRequestTime() {
	return RequestTime;
}
public void setRequestTime(String requestTime) {
	RequestTime = requestTime;
}
@JsonIgnore
public String getPartnerCode() {
	return PartnerCode;
}
public void setPartnerCode(String partnerCode) {
	PartnerCode = partnerCode;
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
public String getTransId() {
	return TransId;
}
public void setTransId(String transId) {
	TransId = transId;
}
@JsonIgnore
public String getTransAmount() {
	return TransAmount;
}
public void setTransAmount(String transAmount) {
	TransAmount = transAmount;
}
@JsonIgnore
public String getTransTime() {
	return TransTime;
}
public void setTransTime(String transTime) {
	TransTime = transTime;
}
@JsonIgnore
public String getBefTransDebt() {
	return BefTransDebt;
}
public void setBefTransDebt(String befTransDebt) {
	BefTransDebt = befTransDebt;
}
@JsonIgnore
public String getAffTransDebt() {
	return AffTransDebt;
}
public void setAffTransDebt(String affTransDebt) {
	AffTransDebt = affTransDebt;
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
