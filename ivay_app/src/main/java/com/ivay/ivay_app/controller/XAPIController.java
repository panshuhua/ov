package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.XAPIService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("star/api")
@Api(tags = "接口联调 - test")
public class XAPIController {
    @Autowired
    private XAPIService xapiService;

    @GetMapping("valCustomerInfoRsp")
    public TransfersRsp validateCustomerInformation(String bankNo, String accNo, String accType) {
        bankNo = "970406";
        accNo = "9704060129837294";
        TransfersRsp transfersRsp = xapiService.validateCustomerInformation(bankNo, accNo, accType);
        return transfersRsp;
    }

    @GetMapping("transfers")
    public TransfersRsp transfers(String bankNo, String accNo, int requestAmount, String memo, String type) {
        bankNo = "970406";
        accNo = "9704060129837294";
        memo = "test api";
        type = "1";
        TransfersRsp transfersRsp = xapiService.transfers(bankNo, accNo, requestAmount, memo, type);
        return transfersRsp;
    }

    @GetMapping("transfersInfo")
    public TransfersRsp transfersInfo(String referenceId) {
        if (StringUtils.isEmpty(referenceId)) {
            referenceId = "f563cd5f095741b780d1a2a2d9f6ea3b"; // todo 值为transfer接口产生的uuid
        }
        TransfersRsp transfersRsp = xapiService.transfersInfo(referenceId);
        return transfersRsp;
    }

    @GetMapping("balance")
    public TransfersRsp balance() {
        TransfersRsp transfersRsp = xapiService.balance();
        return transfersRsp;
    }
}
