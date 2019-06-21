package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ebay还款回调接口实体
 * @author panshuhua
 *
 */
public class XBalanceFuctNoticeReq {
   @JsonProperty("RequestId") 
   private String RequestId;
   @JsonProperty("RequestTime") 
   private String RequestTime;
   @JsonProperty("BankTranTime") 
   private String BankTranTime;
   @JsonProperty("ReferenceId") 
   private String ReferenceId;
   @JsonProperty("MapId") 
   private String MapId;
   @JsonProperty("Amount") 
   private String Amount;
   @JsonProperty("Signature") 
   private String Signature;
   @JsonProperty("MerchantCode") 
   private String MerchantCode;
   @JsonProperty("Fee") 
   private String Fee;
   @JsonProperty("VaName") 
   private String VaName;
   @JsonProperty("VaAcc") 
   private String VaAcc;
   @JsonProperty("BankCode") 
   private String BankCode;
   @JsonProperty("BankName") 
   private String BankName;

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
public String getBankTranTime() {
	return BankTranTime;
}
public void setBankTranTime(String bankTranTime) {
	BankTranTime = bankTranTime;
}
@JsonIgnore
public String getReferenceId() {
	return ReferenceId;
}
public void setReferenceId(String referenceId) {
	ReferenceId = referenceId;
}
@JsonIgnore
public String getMapId() {
	return MapId;
}
public void setMapId(String mapId) {
	MapId = mapId;
}
@JsonIgnore
public String getAmount() {
	return Amount;
}
public void setAmount(String amount) {
	Amount = amount;
}
@JsonIgnore
public String getSignature() {
	return Signature;
}
public void setSignature(String signature) {
	Signature = signature;
}
@JsonIgnore
public String getMerchantCode() {
	return MerchantCode;
}
public void setMerchantCode(String merchantCode) {
	MerchantCode = merchantCode;
}
@JsonIgnore
public String getFee() {
	return Fee;
}
public void setFee(String fee) {
	Fee = fee;
}
@JsonIgnore
public String getVaName() {
	return VaName;
}
public void setVaName(String vaName) {
	VaName = vaName;
}
@JsonIgnore
public String getVaAcc() {
	return VaAcc;
}
public void setVaAcc(String vaAcc) {
	VaAcc = vaAcc;
}
@JsonIgnore
public String getBankCode() {
	return BankCode;
}
public void setBankCode(String bankCode) {
	BankCode = bankCode;
}
@JsonIgnore
public String getBankName() {
	return BankName;
}
public void setBankName(String bankName) {
	BankName = bankName;
}
   
}
