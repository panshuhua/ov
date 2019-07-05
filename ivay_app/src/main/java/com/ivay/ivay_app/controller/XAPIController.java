package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.XAPIService;
import com.ivay.ivay_app.service.XRecordLoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("star/api")
@Api(tags = "baokim借款接口联调 - test")
public class XAPIController {
    @Autowired
    private XAPIService xapiService;

    @GetMapping("valCustomerInfoRsp")
    public TransfersRsp validateCustomerInformation(String bankNo, String accNo, String accType) {
        if (StringUtils.isEmpty(bankNo)) {
            bankNo = "970403";
        }
        if (StringUtils.isEmpty(accNo)) {
            accNo = "060017483539";
        }
        if (StringUtils.isEmpty(accType)) {
            accType = "0";
        }
        TransfersRsp transfersRsp = xapiService.validateCustomerInformation(bankNo, accNo, accType);
        return transfersRsp;
    }

    @GetMapping("transfers")
    public TransfersRsp transfers(String bankNo, String accNo, int requestAmount, String memo, String type) {
        bankNo = "970403";
        accNo = "060017483539";
        memo = "test api";
        type = "0";
        TransfersRsp transfersRsp = xapiService.transfers(bankNo, accNo, requestAmount, memo, type, "");
        return transfersRsp;
    }

    @GetMapping("transfersInfo")
    public TransfersRsp transfersInfo(String referenceId) {
        if (StringUtils.isEmpty(referenceId)) {
            referenceId = "f7a7d9bf26ff49dbae5539e03dfa9fe8"; // 值为transfer接口产生的uuid
        }
        TransfersRsp transfersRsp = xapiService.transfersInfo(referenceId, "");
        return transfersRsp;
    }

    @GetMapping("balance")
    public TransfersRsp balance() {
        TransfersRsp transfersRsp = xapiService.balance();
        return transfersRsp;
    }

    @Autowired
    private XRecordLoanService xRecordLoanService;

    @PostMapping("calcOverDueFee")
    @ApiOperation("手动触发逾期利息的计算")
    public boolean calcOverDueFee() {
        return xRecordLoanService.calcOverDueFee2();
    }

    @GetMapping("test")
    public boolean test(){
        xRecordLoanService.timeoutTransferInfo();
        return true;
    }
}
