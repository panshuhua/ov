package com.ivay.ivay_app.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_app.dto.CollectionTransactionNotice;
import com.ivay.ivay_app.dto.CollectionTransactionRsp;
import com.ivay.ivay_app.dto.EbayBlanceFlucNoticeRsp;
import com.ivay.ivay_app.dto.XBalanceFuctNoticeReq;
import com.ivay.ivay_app.service.XCollectionTransactionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("star/repay")
@Api(tags = "还款回调接口")
public class XCollectionTransactionController {
    @Autowired
    private XCollectionTransactionService xCollectionTransactionService;

    /**
     * baokim
     * 
     * @param notice
     * @return
     * @throws ParseException
     */
    @PostMapping("noticeCollTran")
    @ApiOperation(value = "baokim回调接口")
    public CollectionTransactionRsp noticeCollTran(@RequestBody CollectionTransactionNotice notice)
        throws ParseException {
        CollectionTransactionRsp rsp = xCollectionTransactionService.noticeCollection(notice);
        return rsp;
    }

    @PostMapping("BalanceFuctNotice")
    @ApiOperation(value = "ebay回调接口")
    public EbayBlanceFlucNoticeRsp balanceFuctuationNotification(@RequestBody XBalanceFuctNoticeReq notice)
        throws ParseException {
        EbayBlanceFlucNoticeRsp rsp = xCollectionTransactionService.balanceFuctNotice(notice);
        return rsp;
    }

}
