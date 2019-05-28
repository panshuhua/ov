package com.ivay.ivay_repository.model;

import java.util.Date;

public class XCollectionTransaction {
	private String requestId;
	private String requestTime;
	private String partnerCode;
    private String accNo;
    private String clientidNo;
    private String transId;
    private String transAmount;
    private String transTime;
    private String beftransDebt;
    private String afftransDebt;
    private String accountType;
    private String orderId;
    private Date createTime;
    private Date updateTime;
    private String enableFlag;
    
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getClientidNo() {
		return clientidNo;
	}
	public void setClientidNo(String clientidNo) {
		this.clientidNo = clientidNo;
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(String transAmount) {
		this.transAmount = transAmount;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public String getBeftransDebt() {
		return beftransDebt;
	}
	public void setBeftransDebt(String beftransDebt) {
		this.beftransDebt = beftransDebt;
	}
	public String getAfftransDebt() {
		return afftransDebt;
	}
	public void setAfftransDebt(String afftransDebt) {
		this.afftransDebt = afftransDebt;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	public String getPartnerCode() {
		return partnerCode;
	}
	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getEnableFlag() {
		return enableFlag;
	}
	public void setEnableFlag(String enableFlag) {
		this.enableFlag = enableFlag;
	}
	
    
}
