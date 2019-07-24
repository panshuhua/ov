package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.CustomerService;
import com.ivay.ivay_repository.dto.CustomerInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value = "基础信息列表")
    @PostMapping("listBasicInfos")
    public Response<PageTableResponse> listBasicInfos(@RequestParam(required = false, defaultValue = "0") int limit,
                                                      @RequestParam(required = false, defaultValue = "1") int num,
                                                      @RequestBody(required = false) CustomerInfo customerInfo) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(customerService.listBasicInfo(limit, num, customerInfo));
        return response;
    }

    @GetMapping(value = "listContactInfos")
    @ApiOperation(value = "联系人列表")
    public Response<PageTableResponse> listContactInfos(@RequestParam(required = false, defaultValue = "0") int limit,
                                                        @RequestParam(required = false, defaultValue = "1") int num,
                                                        @RequestParam(required = true) String userGid) {
        /*return new PageTableHandler(
                a -> customerService.countContactInfo(a.getParams()),
                a -> customerService.listContactInfo(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);*/

        Response<PageTableResponse> response = new Response<>();
        response.setBo(customerService.listContactInfo(userGid, num, limit));
        return response;
    }

    @GetMapping(value = "listLoans")
    @ApiOperation(value = "借款列表")
    public Response<PageTableResponse> listLoans(@RequestParam(required = false, defaultValue = "0") int limit,
                                       @RequestParam(required = false, defaultValue = "1") int num,
                                       @RequestParam(required = true) String userGid) {
        /*return new PageTableHandler(
                a -> customerService.countLoan(a.getParams()),
                a -> customerService.listLoan(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);*/
        Response<PageTableResponse> response = new Response<>();
        response.setBo(customerService.listLoan(userGid, num, limit));
        return response;
    }

    @GetMapping(value = "listRepays")
    @ApiOperation(value = "还款列表")
    public Response<PageTableResponse> listRepays(@RequestParam(required = false, defaultValue = "0") int limit,
                                                  @RequestParam(required = false, defaultValue = "1") int num,
                                                  @RequestParam(required = true) String userGid) {
        /*return new PageTableHandler(
                a -> customerService.countRepay(a.getParams()),
                a -> customerService.listRepay(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);*/
        Response<PageTableResponse> response = new Response<>();
        response.setBo(customerService.listRepay(userGid, num, limit));
        return response;
    }

    @GetMapping(value = "listBanks")
    @ApiOperation(value = "银行列表")
    public Response<PageTableResponse> listBanks(@RequestParam(required = false, defaultValue = "0") int limit,
                                                 @RequestParam(required = false, defaultValue = "1") int num,
                                                 @RequestParam(required = true) String userGid) {
        /*return new PageTableHandler(
                a -> customerService.countBank(a.getParams()),
                a -> customerService.listBank(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);*/
        Response<PageTableResponse> response = new Response<>();
        response.setBo(customerService.listBank(userGid, num, limit));
        return response;
    }

}
