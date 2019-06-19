package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.CollectionTransactionNotice;
import com.ivay.ivay_app.dto.CollectionTransactionRsp;
import com.ivay.ivay_app.service.XCollectionTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("star/repay")
@Api(tags = "还款回调接口")
public class XCollectionTransactionController {
    @Autowired
    private XCollectionTransactionService xCollectionTransactionService;

    /**
     * baokim
     * @param notice
     * @return
     * @throws ParseException
     */
    @PostMapping("noticeCollTran")
    @ApiOperation(value = "回调接口")
    public CollectionTransactionRsp noticeCollTran(@RequestBody CollectionTransactionNotice notice) throws ParseException {
        CollectionTransactionRsp rsp = xCollectionTransactionService.noticeCollection(notice);
        return rsp;
    }


}
