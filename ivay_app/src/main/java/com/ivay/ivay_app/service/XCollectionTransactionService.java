package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.CollectionTransactionNotice;
import com.ivay.ivay_app.dto.CollectionTransactionRsp;
import com.ivay.ivay_repository.model.XCollectionTransaction;

import java.text.ParseException;

public interface XCollectionTransactionService {
	
   String getRequestId(String PartnerCode, String date);
   
   boolean checkRequestId(String requestId);
   
   int insert(XCollectionTransaction xCollectionTransaction);
   
   long getCollectAmount(String accNo);
   
   CollectionTransactionRsp noticeCollection(CollectionTransactionNotice notice)throws ParseException;
   
}
