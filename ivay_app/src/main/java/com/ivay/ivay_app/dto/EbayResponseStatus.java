package com.ivay.ivay_app.dto;

public enum EbayResponseStatus {
	 SUCCESS("200", "Successful"),
	 TRANSACTION_PENDING("99", "Transaction pending"),
	 TRANSACTION_FAILED("11", "Transaction failed"),
	 EPAY_ERROR("101", "Error processing from EPAY"),
	 DUPLICATED_REQUESTID("102", "Duplicated RequestId"),
	 INCORRECT_SIGNATURE("103", "Incorrect signature"),
	 INCORRECT_PARTNERCODE("110", "Incorrect PartnerCode"),
	 PARTNERCODE_DELETED("111", "PartnerCode deleted from the system"),
	 PARTNERCODE_NOACTIVATED("112", "PartnerCode not yet activated"),
	 OPERATIONCODE_REQUIRED("113", "Operation code is required"),
	 INCORRECT_OPERATIONCODE("114", "Incorrect Operation code"),
	 BANKID_REQUIRED("115", "BankID is required"),
	 BANKID_NOTSUPPORTED("116", "BankID not supported"),
	 ACCOUNTNO_LENGTH("117", "Account no. /Card no. should be from 4-22 characters in length"),
	 INVALID_ACCOUNTNO("118", "Invalid account no./Card no."),
	 ACCOUNTNO_EXIST("119", "Account no./Card no. does not exist"),
	 INCORRECT_ACCOUNTTYPE("120", "Incorrect account type"),
	 TRANSACTIONID_REQUIRED("121", "Transaction ID sent from Partner is required"),
	 TRANSACTIONID_ISEXISTING("122", "Transaction ID sent by Partner is existing"),
	 TRANSACTION_NOTFOUND("123", "Transaction not found"),
	 TRANSFERAMOUNT_REQUIRED("124", "Transfer amount required"),
	 INVALID_TRANSFERAMOUNT("125", "Invalid transfer amount"),
	 ERRORPROCESS_EPAYANDBANK("126", "Error processing between EPAY and bank"),
	 ERRORCONNECT_TOBANK("127", "Error connecting to bank"),
	 ERRORPROCESS_BANK("128", "Error processing from bank"),
	 INSUFFICIENTLIMIT_EXPIREDPERIOD("129", "Insufficient disbursement limit or expired guarantee period"),
	 CARDTYPE_NOTSUPPORT("130", "This card type is not support"),
	 CONTRACTNUMBER_EXIST("131", "The contract number already exist"),
	 CONTRACTNUMBER_REQUIRED("133", "Contract number is required"),
	 REQUESTID_REQUIRED("401", "The RequestId is required"),
	 REQUESTTIME_REQUIRED("402", "The RequestTime is required"),
	 REQUESTTIME_INVALID("403", "The RequestTime is invalid"),
	 PARTNERCODE_REQUIRED("404", "The PartnerCode is required"),
	 DATAREQUEST_NULL("405", "All data request null"),
	 NOTICE_SUCCESS("200","Push notify successful to Merchant"),
	 NOTICE_TIMEOUT("99","Timeout"),
	 NOTICE_FAIL("11","Fail"),
	 NOTICE_DUPLICATE_REFERENCEID("102","Duplication in ReferenceId (Merchant accepted Notification before)"),
	 NOTICE_SIGNATURE_WRONG("103","Signature wrong"),
	 NOTICE_PARTNERCODE_WRONG("110","PartnerCode wrong"),
	 NOTICE_MISSING_FIELD("124","The required field is not entered, any missing field will be specified in Message"),
	 NOTICE_AMOUNT_WRONG("125","Amount wrong"),
	 ;
	 
	 EbayResponseStatus(String code, String message) {
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
