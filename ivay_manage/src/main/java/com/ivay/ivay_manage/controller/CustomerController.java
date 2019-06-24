package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客服系统
 *
 * @author psh
 */
@RestController
@RequestMapping("manage/customer")
@Api(tags = "客服系统")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping(params = {"start", "length"}, value = "listBasicInfos")
    @ApiOperation(value = "基础信息列表")
    public PageTableResponse listBasicInfos(PageTableRequest request) {
        return new PageTableHandler(
                a -> customerService.countBasicInfo(a.getParams()),
                a -> customerService.listBasicInfo(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @GetMapping(params = {"start", "length"}, value = "listContactInfos")
    @ApiOperation(value = "联系人列表")
    public PageTableResponse listContactInfos(PageTableRequest request) {
        return new PageTableHandler(
                a -> customerService.countContactInfo(a.getParams()),
                a -> customerService.listContactInfo(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @GetMapping(params = {"start", "length"}, value = "listLoans")
    @ApiOperation(value = "借款列表")
    public PageTableResponse listLoans(PageTableRequest request) {
        return new PageTableHandler(
                a -> customerService.countLoan(a.getParams()),
                a -> customerService.listLoan(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @GetMapping(params = {"start", "length"}, value = "listRepays")
    @ApiOperation(value = "还款列表")
    public PageTableResponse listRepays(PageTableRequest request) {
        return new PageTableHandler(
                a -> customerService.countRepay(a.getParams()),
                a -> customerService.listRepay(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @GetMapping(params = {"start", "length"}, value = "listBanks")
    @ApiOperation(value = "银行列表")
    public PageTableResponse listBanks(PageTableRequest request) {
        return new PageTableHandler(
                a -> customerService.countBank(a.getParams()),
                a -> customerService.listBank(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

}
