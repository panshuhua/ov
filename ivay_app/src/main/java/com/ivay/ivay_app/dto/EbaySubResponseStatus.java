package com.ivay.ivay_app.dto;

public enum EbaySubResponseStatus {
	 //Transaction pending
	 WRBinternal_timeout("0010", "WRB internal timeout"),
	 WRBinternal_systemerror("0011", "WRB internal system error"),
	 Transaction_cutofftime("0053", "Transaction in cut-off time (between 00:00 and 00:05 every day)"),
	 WRBinternal_systemerror2("0062", "WRB internal system error"),
	 WRBinternal_systemerror_Firmbanking("0105", "WRB internal system error (Firm banking system error)"),
	 Timeout_NAPASBeneficiary_bank("7068", "Timeout between NAPAS and Beneficiary bank"),
	 WRB_Firmbanking_systemerror("8000", "WRB Firmbanking system error"),
	 WRB_Firmbanking_systemerror2("8011", "WRB Firmbanking system error"),
	 WRB_Firmbanking_systemerror3("8012", "WRB Firmbanking system error"),
	 WRBinternal_systemerror3("9900", "WRB internal system error"),
	 WRBinternal_systemerror4("9902", "WRB internal system error"),
	 WRBinternal_systemerror5("9903", "WRB internal system error"),
	 Timeout_EpayWoori_bank("5101", "Timeout between Epay and Woori bank"),
	 
	 //Transaction  failed
	 MissingMandatoryField_requestmessage("0002", "Missing Mandatory Field in the request message"),
	 Wrong_datatype("0003", "Wrong data type"),
	 WRBInternaldata_queryissue("0004", "WRB Internal data query issue"),
	 WRBInternaldata_queryissue2("0006", "WRB Internal data query issue"),
	 WRBInternaldata_queryissue3("0009", "WRB Internal data query issue"),
	 WRBbeneficiary_accountissue("0033", "WRB beneficiary account issue"),
	 Currency_issue("0036", "Currency issue"),
	 WRBinternal_systemerror6("0045", "WRB internal system error"),
	 WRBbeneficiary_accountissue2("0063", "WRB beneficiary account issue"),
	 EPAYfirm_accountissue("0069", "EPAY firm account issue"),
	 WRBinternal_systemerror7("0111", "WRB internal system error"),
	 Beneficiary_accountissue("7001", "Beneficiary account issue"),
	 Beneficiary_accountissue2("7005", "Beneficiary account issue"),
	 Error_beneficiary_bank("7012", "Error occurred during the internal deposit process of beneficiary bank"),
	 Invalid_amount("7013", "Invalid amount"),
	 Wrongbeneficiary_accountinfo("7014", "Wrong beneficiary account information"),
	 Beneficiarybank_systemissue("7019", "Beneficiary bank system issue"),
	 Beneficiarybank_systemissue2("7028", "Beneficiary bank system issue"),
	 Beneficiarybank_msgformat_error("7030", "Beneficiary bank msg format error"),
	 Beneficiarybank_transveriferror("7034", "Beneficiary bank transaction verification error - VIETIN BANK"),
	 Beneficiary_cardissue("7036", "Beneficiary card issue"),	
	 Beneficiary_cardissue2("7042", "Beneficiary card issue (LIEN VIET POST BANK)"),
	
	 Beneficiary_cardissue3("7054", "Beneficiary card issue (expired card or lost card)"),	
	 Beneficiary_accountissue3("7057", "Beneficiary account issue"),	
	 Beneficiarybank_transactionVerificationError("7059", "Beneficiary bank transaction verification error"),	
	 Beneficiarybank_transactionVerificationError2("7063", "Beneficiary bank transaction verification error"),	
	 Beneficiarybank_systemissue_MBBANK("7069", "Beneficiary bank system issue - MB BANK"),	
	 Beneficiarybank_systemissue_OCB("7073", "Beneficiary bank system issue - OCB"),	
	 Beneficiary_cardissue4("7075", "Beneficiary card issue (relate length of password)"),	
	 Invalid_beneficiaryaccount("7076", "Invalid beneficiary account"),
	 Beneficiarybank_transactionVerificationError3("7088","Beneficiary bank transaction verification error"),
	 Beneficiarybank_transactionVerificationError4("7089", "Beneficiary bank transaction verification error"),
	 Beneficiarybank_systemerror("7091", "Beneficiary bank system error"),
	 NAPAS_systemerror("7092", "NAPAS system error"),
	 NAPAS_systemerror2("7093", "NAPAS system error"),
	 Duplicatetransaction_WRBNapas("7094", "Duplicate transaction between WRB and Napas"),
	 Beneficiarybank_systemissue3("7096","Beneficiary bank system issue"),
     Beneficiarybank_systemissue_MBBANK2("7097", "Beneficiary bank system issue - MB BANK"),
	 WRBinternal_systemerror8("9901", "WRB internal system error"),
	 Transaction_Failed("5555", "Transaction failed (Pending transaction updated to failed)"),
	 ;
	 
	 EbaySubResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
	 }

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
	    
}
