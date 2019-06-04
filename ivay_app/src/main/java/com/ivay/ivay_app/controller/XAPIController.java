package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.XAPIService;
import com.ivay.ivay_app.service.XRecordLoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("star/api")
@Api(tags = "接口联调 - test")
public class XAPIController {
    @Autowired
    private XAPIService xapiService;

    @GetMapping("valCustomerInfoRsp")
    public TransfersRsp validateCustomerInformation(String bankNo, String accNo, String accType) {
        bankNo = "970403";
        accNo = "060017483539";
        accType = "0";
        TransfersRsp transfersRsp = xapiService.validateCustomerInformation(bankNo, accNo, accType);
        return transfersRsp;
    }

    @GetMapping("transfers")
    public TransfersRsp transfers(String bankNo, String accNo, int requestAmount, String memo, String type) {
        bankNo = "970403";
        accNo = "060017483539";
        memo = "test api";
        type = "0";
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

    @Autowired
    private XRecordLoanService xRecordLoanService;
    private static final Logger logger = LoggerFactory.getLogger(XAPIController.class);

    @PostMapping("calcOverDueFee")
    @ApiOperation("测试逾期利息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "diff", value = "额外逾期时间", dataType = "Long", paramType = "query", defaultValue = "0")
    })
    public boolean calcOverDueFee(@RequestParam(required = false) Integer diff) {
        int count = 0;
        String start = "开始计算逾期费用---start";
        while (true) {
            if (count > 0) {
                start = "正在进行第" + count + "次重试--start";
            }
            logger.info(start);
            boolean flag = xRecordLoanService.calcOverDueFee2ForTest(diff == null ? 0 : diff);
            logger.info("逾期费用计算结束---{}", flag ? "成功" : "失败");
            if (flag) {
                return true;
            } else {
                if ((count++ > 5)) {
                    logger.error("逾期费用计算出错，请及时查看");
                    return false;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        logger.error("逾期费用计算暂停异常: {}", ex);
                    }
                }
            }
        }
    }
}
