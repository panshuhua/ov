package com.ivay.ivay_repository.model;

public class XEbayTransfersInfo extends XBaokimTransfersInfo{
	private static final long serialVersionUID = -8466951745963476837L;
	
	private String contractNumber;
	private String extend;
	private String subErrorCode;
	private String subErrorMessage;
	private String reason;
	
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getSubErrorCode() {
		return subErrorCode;
	}
	public void setSubErrorCode(String subErrorCode) {
		this.subErrorCode = subErrorCode;
	}
	public String getSubErrorMessage() {
		return subErrorMessage;
	}
	public void setSubErrorMessage(String subErrorMessage) {
		this.subErrorMessage = subErrorMessage;
	}
	
}

