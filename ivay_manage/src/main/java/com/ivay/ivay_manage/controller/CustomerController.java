package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.model.*;
import com.ivay.ivay_manage.service.CustomerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 客服系统
 *
 * @author psh
 */
@RestController
@RequestMapping("customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping(params = {"start", "length"}, value = "listBasicInfos")
    @ApiOperation(value = "列表")
    public PageTableResponse listBasicInfos(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return customerService.countBasicInfo(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<XUserInfo> list(PageTableRequest request) {
                return customerService.listBasicInfo(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @GetMapping(params = {"start", "length"}, value = "listContactInfos")
    @ApiOperation(value = "列表")
    public PageTableResponse listContactInfos(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return customerService.countContactInfo(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<XUserExtInfo> list(PageTableRequest request) {
                return customerService.listContactInfo(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @GetMapping(params = {"start", "length"}, value = "listLoans")
    @ApiOperation(value = "列表")
    public PageTableResponse listLoans(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return customerService.countLoan(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<XRecordLoan> list(PageTableRequest request) {
                return customerService.listLoan(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @GetMapping(params = {"start", "length"}, value = "listRepays")
    @ApiOperation(value = "列表")
    public PageTableResponse listRepays(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return customerService.countRepay(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<XRecordRepayment> list(PageTableRequest request) {
                return customerService.listRepay(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @GetMapping(params = {"start", "length"}, value = "listBanks")
    @ApiOperation(value = "列表")
    public PageTableResponse listBanks(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return customerService.countBank(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<XUserBankcoadInfo> list(PageTableRequest request) {
                return customerService.listBank(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

}
