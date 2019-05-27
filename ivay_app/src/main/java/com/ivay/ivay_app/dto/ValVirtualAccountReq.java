package com.ivay.ivay_app.dto;

import java.io.Serializable;

public class ValVirtualAccountReq implements Serializable {

	private static final long serialVersionUID = 35435494737580569L;

	private String RequestId;
	private String RequestTime;
	private String PartnerCode;
	private String Operation;
	private String AccName;
	private String ClientIdNo;
	private String IssuedDate;
	private String IssuedPlace;
	private String CollectAmount;
	private String ExpireDate;
	private String AccountType;
	private String OrderId;
	private String Signature;
	private String ReferenceId;
	private String AccNo;

	public String getRequestId() {
		return RequestId;
	}

	public void setRequestId(String requestId) {
		RequestId = requestId;
	}

	public String getRequestTime() {
		return RequestTime;
	}

	public void setRequestTime(String requestTime) {
		RequestTime = requestTime;
	}

	public String getPartnerCode() {
		return PartnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		PartnerCode = partnerCode;
	}

	public String getOperation() {
		return Operation;
	}

	public void setOperation(String operation) {
		Operation = operation;
	}

	public String getAccName() {
		return AccName;
	}

	public void setAccName(String accName) {
		AccName = accName;
	}

	public String getClientIdNo() {
		return ClientIdNo;
	}

	public void setClientIdNo(String clientIdNo) {
		ClientIdNo = clientIdNo;
	}

	public String getIssuedDate() {
		return IssuedDate;
	}

	public void setIssuedDate(String issuedDate) {
		IssuedDate = issuedDate;
	}

	public String getIssuedPlace() {
		return IssuedPlace;
	}

	public void setIssuedPlace(String issuedPlace) {
		IssuedPlace = issuedPlace;
	}

	public String getCollectAmount() {
		return CollectAmount;
	}

	public void setCollectAmount(String collectAmount) {
		CollectAmount = collectAmount;
	}

	public String getExpireDate() {
		return ExpireDate;
	}

	public void setExpireDate(String expireDate) {
		ExpireDate = expireDate;
	}

	public String getAccountType() {
		return AccountType;
	}

	public void setAccountType(String accountType) {
		AccountType = accountType;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		OrderId = orderId;
	}

	public String getSignature() {
		return Signature;
	}

	public void setSignature(String signature) {
		Signature = signature;
	}

	public String getAccNo() {
		return AccNo;
	}

	public void setAccNo(String accNo) {
		AccNo = accNo;
	}

	public String getReferenceId() {
		return ReferenceId;
	}

	public void setReferenceId(String referenceId) {
		ReferenceId = referenceId;
	}

	
}
