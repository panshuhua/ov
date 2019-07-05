package com.ivay.ivay_repository.dto;

import lombok.Data;

@Data
public class XTimeoutTransferInfo {
    private String loanGid;
    private String referenceId;

    private String responseCode;
    private String responseMessage;
    private String transactionTime;
    private String transferAmount;
}
