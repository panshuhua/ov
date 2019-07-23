package com.ivay.ivay_app.service;

import java.text.ParseException;

import com.ivay.ivay_app.dto.CollectionTransactionNotice;
import com.ivay.ivay_app.dto.CollectionTransactionRsp;
import com.ivay.ivay_app.dto.EbayBlanceFlucNoticeRsp;
import com.ivay.ivay_app.dto.XBalanceFuctNoticeReq;

public interface XCollectionTransactionService {

    String getRequestId(String partnerCode, String date);

    boolean checkRequestId(String requestId);

    long getCollectAmount(String accNo);

    CollectionTransactionRsp noticeCollection(CollectionTransactionNotice notice) throws ParseException;

    EbayBlanceFlucNoticeRsp balanceFuctNotice(XBalanceFuctNoticeReq notice);

}
