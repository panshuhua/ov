package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.ValVirtualAccountRsp;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XRecordRepayment;
import com.ivay.ivay_repository.model.XVirtualAccount;

public interface XVirtualAccountService {

    String getRequestId(String partnerCode, String date);

    XVirtualAccount queryVirtualAccount(String id);

    ValVirtualAccountRsp addVirtualAccount(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment);

    XVirtualAccount selectByOrderId(String orderId);

    ValVirtualAccountRsp updateXVirtualAccount(XVirtualAccount xVirtualAccount, Long collectAmount);

    boolean saveVirtualAccount();

}
