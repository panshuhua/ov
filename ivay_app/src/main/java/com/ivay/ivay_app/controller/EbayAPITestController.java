package com.ivay.ivay_app.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_app.dto.EbayTransfersRsp;
import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.XEbayAPIService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("star/ebayApi")
@Api(tags = "Ebay接口联调 - test")
public class EbayAPITestController {
	@Autowired
    private XEbayAPIService xEbayApiService;

    @GetMapping("valCustomerInfoRsp")
    public TransfersRsp validateCustomerInformation(String bankNo, String accNo, String accType) {
        bankNo = "970403";
        accNo = "0600174835390000"; //ebay的accNo末尾必须有4个0，即"0000"，否则就不会返回200
        accType = "0";
        EbayTransfersRsp transfersRsp = xEbayApiService.validateCustomerInformation(bankNo, accNo, accType);
        return transfersRsp;
    }

    @GetMapping("transfers")
    public EbayTransfersRsp transfers(String bankNo, String accNo, Integer requestAmount, String memo, String type) {
        bankNo = "970403";
        accNo = "0600174835390000";
        memo = "test api";
        type = "0";
        String accountName = "";
        String contractNumber = "";
        String extend = "";
        requestAmount = 5000000;
        EbayTransfersRsp transfersRsp = xEbayApiService.transfers(bankNo, accNo, requestAmount, memo, type,accountName,contractNumber,extend);
        return transfersRsp;
    }

    @GetMapping("transfersInfo")
    public EbayTransfersRsp transfersInfo(String referenceId) {
        if (StringUtils.isEmpty(referenceId)) {
            referenceId = "e5811c124b69431887a0dad94062f983"; // todo 值为transfer接口产生的uuid
        }
        EbayTransfersRsp transfersRsp = xEbayApiService.transfersInfo(referenceId);
        return transfersRsp;
    }

    @GetMapping("balance")
    public EbayTransfersRsp balance() {
    	EbayTransfersRsp transfersRsp = xEbayApiService.balance();
        return transfersRsp;
    }

}
