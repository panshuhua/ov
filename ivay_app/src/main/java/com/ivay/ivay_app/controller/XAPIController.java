package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.XAPIService;
import com.ivay.ivay_app.service.XRecordLoanService;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dto.XOverDueFee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        TransfersRsp transfersRsp = xapiService.transfers(bankNo, accNo, requestAmount, memo, type);
        return transfersRsp;
    }

    @GetMapping("transfersInfo")
    public TransfersRsp transfersInfo(String referenceId) {
        if (StringUtils.isEmpty(referenceId)) {
            referenceId = "f563cd5f095741b780d1a2a2d9f6ea3b"; // 值为transfer接口产生的uuid
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
    @ApiOperation("手动触发逾期利息的计算")
    public boolean calcOverDueFee() {
        boolean flag = false;
        int count = 0;
        String start = "开始计算逾期费用---start";
        while (!flag) {
            if (count > 0) {
                start = "正在进行第" + count + "次重试--start";
            }
            logger.info(start);
            flag = xRecordLoanService.calcOverDueFee2();
            logger.info("逾期费用计算结束---{}", flag ? "成功" : "失败");
            if (!flag) {
                if ((count++ > 5)) {
                    flag = true;
                    logger.error("逾期费用计算出错，请及时查看");
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        logger.error("逾期费用计算暂停异常: {}", ex);
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @Autowired
    private XRecordLoanDao xRecordLoanDao;

    @GetMapping("test")
    public boolean test(String key) {
        List<XOverDueFee> xOverDueFeeList = xRecordLoanDao.findOneOverdue();
        xRecordLoanService.calc1DayOverDueFee(xOverDueFeeList);
        return true;
    }
}
