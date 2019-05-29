package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.ValVirtualAccountRsp;
import com.ivay.ivay_app.model.XRecordLoan;
import com.ivay.ivay_app.model.XRecordRepayment;
import com.ivay.ivay_app.model.XVirtualAccount;

public interface XVirtualAccountService {

    String getRequestId(String PartnerCode, String date);

    XVirtualAccount queryVirtualAccount(String id);

    ValVirtualAccountRsp addVirtualAccount(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment);

    XVirtualAccount selectByOrderId(String orderId);

    ValVirtualAccountRsp updateXVirtualAccount(XVirtualAccount xVirtualAccount, Long collectAmount);
}
