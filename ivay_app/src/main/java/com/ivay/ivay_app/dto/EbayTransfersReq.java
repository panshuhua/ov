package com.ivay.ivay_app.dto;

public class EbayTransfersReq extends TransfersReq{
	
	private static final long serialVersionUID = -4437279501439933852L;
	
	//交易：transfer money
    private String AccountName;
    private String ContractNumber;
    private String Extends;
    
	public String getAccountName() {
		return AccountName;
	}
	public void setAccountName(String accountName) {
		AccountName = accountName;
	}
	public String getContractNumber() {
		return ContractNumber;
	}
	public void setContractNumber(String contractNumber) {
		ContractNumber = contractNumber;
	}
	public String getExtends() {
		return Extends;
	}
	public void setExtends(String extends1) {
		Extends = extends1;
	}
    
}
