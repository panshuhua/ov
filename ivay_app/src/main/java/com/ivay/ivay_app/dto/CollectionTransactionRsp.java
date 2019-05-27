package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CollectionTransactionRsp {
   @JsonProperty("ResponseCode")
   private String ResponseCode;
   @JsonProperty("ResponseMessage")
   private String ResponseMessage;
   @JsonProperty("ReferenceId")
   private String ReferenceId;
   @JsonProperty("AccNo")
   private String AccNo;
   @JsonProperty("AffTransDebt")
   private String  AffTransDebt;
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
public String getReferenceId() {
	return ReferenceId;
}
public void setReferenceId(String referenceId) {
	ReferenceId = referenceId;
}
@JsonIgnore
public String getAccNo() {
	return AccNo;
}
public void setAccNo(String accNo) {
	AccNo = accNo;
}
@JsonIgnore
public String getAffTransDebt() {
	return AffTransDebt;
}
public void setAffTransDebt(String affTransDebt) {
	AffTransDebt = affTransDebt;
}
@JsonIgnore
public String getSignature() {
	return Signature;
}
public void setSignature(String signature) {
	Signature = signature;
}
   
}
