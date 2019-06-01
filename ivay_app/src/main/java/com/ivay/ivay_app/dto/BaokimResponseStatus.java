package com.ivay.ivay_app.dto;

public enum BaokimResponseStatus {
    SUCCESS("200", "Successful"),
    TIMEOUT("99", "Transaction timeout"),
    FAIL("11", "Failed"),
    ERROR("101", "Error processing from BAOKIM "),
    DuplicatedRequestId("102", "Duplicated RequestId"),
    InvalidSignature("103", "Incorrect signature"),
    InvalidPartnerCode("110", "Incorrect PartnerCode"),
    DeletedPartnerCode("111", "PartnerCode deleted from the system"),
    InactivatedPartnerCode("112", "PartnerCode not yet activated"),
    RequiredOperation("113", "Operation code is required"),
    InvalidOperation("114", "Incorrect Operation code"),
    RequiredBankID("115", "BankID is required"),
    InvalidBankID("116", "BankID not supported"),
    Account("117", "Account no. /Card no. should be from 8-22 characters in length"),
    InvalidAccount("118", "Invalid account no./Card no."),
    NotExitAccount("119", "Account no./Card no. does not exist"),
    InvalidAccountType("120", "Incorrect account type"),
    RequiredTransactionID("121", "Transaction ID sent from Partner is required"),
    ExistTransactionID("122", "Transaction ID sent by Partner is existing"),
    NonTransaction("123", "Transaction unfound"),
    NonAmount("124", "Transfer amount required"),
    InvalidAmount("125", "Invalid transfer amount"),
    ErrorBaokimProcessing("126", "Error processing between BAOKIM and bank"),
    ErrorConnecting("127", "Error connecting to bank"),
    ErrorBankProcessing("128", "Error processing from bank"),
    Error("129", "Insufficient disbursement limit or expired guarantee period"),
    CollectionSuccess("200","Success"),
    Pending("99","Pending"),
    Fail("11","Fail"),
    BAOKIMError("101","Error from BAOKIM"),
    BankError("102","Error from Bank"),
    IncorrectOperation("103","Operation is incorrect"),
    IncorrectRequestId("104","RequestId or RequestTime is incorrect"),
    IncorrectPartnerCode("105","PartnerCode is incorrect"),
    IncorrectAccName("106","AccName is incorrect"),
    IncorrectClientIdNo("107","ClientIdNo is incorrect"),
    IncorrectIssued("108","IssuedDate or IssuedPlace is incorrect"),
    IncorrectCollectAmount("109","CollectAmount is incorrect"),
    IncorrectExpireDate("110","ExpireDate is incorrect"),
    IncorrectAccNo("111","AccNo is incorrect"),
    IncorrectRefferenceId("113","RefferenceId is not valid"),
    IncorrectTransAmount("115","TransAmount is incorrect"),
    IncorrectTransTime("116","TransTime is incorrect"),
    IncorrectBefTransDebt("117","BefTransDebt is incorrect"),
    IncorrectTransId("118","TransId is incorrect"),
    IncorrectAffTransDebt("119","AffTransDebt is incorrect"),
    IncorrectSignature("120","Signature is incorrect"),
    IncorrectAccountType("121","AccountType is incorrect"),
    IncorrectOrderId("122","OrderId is incorrect"),
    IncorrectTransIdRepeat("123","TransId is exists"),
    ;


    BaokimResponseStatus(String code, String message) {
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
