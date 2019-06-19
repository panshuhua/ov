package com.ivay.ivay_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_app.dto.EbayVirtualAccountRsp;
import com.ivay.ivay_app.service.XEbayRepayAPIService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("star/ebayRepayApi")
@Api(tags = "Ebay还款接口联调 - test")
public class XEbayRepayAPITestController {
	@Autowired
    private XEbayRepayAPIService xEbayRepayAPIService;

    @GetMapping("registVitualAccount")
    public EbayVirtualAccountRsp registVitualAccount(String mapId, Number amount, String customerName) throws Exception {
    	EbayVirtualAccountRsp rsp=xEbayRepayAPIService.registVitualAccount(mapId, amount, customerName);
		return rsp;
    }
    
    @GetMapping("updateVitualAccount")
    public EbayVirtualAccountRsp updateVitualAccount(String mapId, Number amount, String customerName,String accountNo) throws Exception {
    	EbayVirtualAccountRsp rsp=xEbayRepayAPIService.updateVitualAccount(mapId, amount, customerName,accountNo);
		return rsp;
    }
    
    @GetMapping("cancelMapping")
    public EbayVirtualAccountRsp cancelMapping(String mapId,String accountNo) throws Exception {
    	EbayVirtualAccountRsp rsp=xEbayRepayAPIService.cancelMapping(mapId, accountNo);
		return rsp;
    }
    
    @GetMapping("viewMappingStatus")
    public EbayVirtualAccountRsp viewMappingStatus(String requestId) throws Exception {
    	EbayVirtualAccountRsp rsp=xEbayRepayAPIService.viewMappingStatus(requestId);
		return rsp;
    }
    
}
