package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.XAPIService;
import com.ivay.ivay_app.service.XRecordLoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

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

    @GetMapping("calcOverDueFee")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dueAmount", value = "剩余本金", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "day", value = "逾期天数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "loanRate", value = "借款利率", dataType = "String", paramType = "query", defaultValue = "0.21"),
            @ApiImplicitParam(name = "loanPeriod", value = "贷款天数", dataType = "Long", paramType = "query", defaultValue = "7")
    })
    @ApiOperation("计算逾期费用")
    public long calcOverDueFee(@RequestParam long dueAmount,
                               @RequestParam(required = false) int day,
                               @RequestParam(required = false) String loanRate,
                               @RequestParam(required = false) int loanPeriod) {
        return xRecordLoanService.calcOverDueFee(dueAmount, day, new BigDecimal(loanRate), loanPeriod);
    }

    @GetMapping("test")
    public boolean test() {
        xRecordLoanService.timeoutTransferInfo();
        return true;
    }
}
