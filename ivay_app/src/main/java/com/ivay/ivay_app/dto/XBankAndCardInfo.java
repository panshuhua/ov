package com.ivay.ivay_app.dto;

import com.ivay.ivay_app.model.XUserBankcardInfo;

public class XBankAndCardInfo extends XUserBankcardInfo {
    private String bankNo;
    private String bankName;

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
