package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.EbayVirtualAccountReq;
import com.ivay.ivay_app.dto.EbayVirtualAccountRsp;

public interface XEbayRepayAPIService {
	
	EbayVirtualAccountRsp registVitualAccount(String mapId,Number amount,String customerName) throws Exception;
	
	EbayVirtualAccountRsp updateVitualAccount(String mapId,Number amount,String customerName,String accountNo) throws Exception;
	
	EbayVirtualAccountRsp cancelMapping(String mapId,String accountNo) throws Exception;
	
	EbayVirtualAccountRsp viewMappingStatus(EbayVirtualAccountReq ebayDataObjectReq) throws Exception;
	
	EbayVirtualAccountRsp BalanceFlucfluctuationNotice(EbayVirtualAccountReq ebayDataObjectReq) throws Exception;
}
