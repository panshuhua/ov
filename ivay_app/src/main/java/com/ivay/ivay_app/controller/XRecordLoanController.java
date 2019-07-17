package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.service.XAPIService;
import com.ivay.ivay_app.service.XLoanRateService;
import com.ivay.ivay_app.service.XRecordLoanService;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.model.XRecordLoan;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "借款")
@RestController
@RequestMapping("star")
public class XRecordLoanController {
    @Autowired
    private XLoanRateService xLoanRateService;

    @Autowired
    private XRecordLoanService xRecordLoanService;

    @GetMapping("loanRate/list")
    @ApiOperation(value = "获取某用户的借款利率")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query",
                    defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),})
    @LogAnnotation(module = "获取某用户的借款利率")
    public Response<PageTableResponse> loanRateList(@RequestParam(required = false, defaultValue = "0") int limit,
                                                    @RequestParam(required = false, defaultValue = "1") int num, @RequestParam String userGid,
                                                    HttpServletRequest request) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xLoanRateService.list(limit, num, userGid));
        return response;
    }

    @PostMapping("loanRecord/add")
    @ApiOperation(value = "申请借款")
    @ApiImplicitParams({@ApiImplicitParam(name = "password", value = "交易密码", dataType = "String", paramType = "query", required = true)})
    @LogAnnotation(module = "申请借款")
    public Response<String> loanRecordSave(@RequestBody XRecordLoan xRecordLoan,
                                           @RequestParam String password,
                                           HttpServletRequest request) {
        String result = xRecordLoanService.borrowMoney(xRecordLoan, password);
        if (!StringUtils.isEmpty(result)) {
            // 目前是借款余额不足会给提示
            throw new BusinessException(result);
        }
        Response<String> response = new Response<>();
        response.setBo(xRecordLoan.getOrderId());
        return response;
    }

    @GetMapping("loanRecord/get")
    @ApiOperation(value = "查看借款详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gid", value = "借款gid", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query",
                    required = true)})
    @LogAnnotation(module = "查看借款详情")
    public Response<XRecordLoan> get(@RequestParam String gid, @RequestParam String userGid,
                                     HttpServletRequest request) {
        Response<XRecordLoan> response = new Response<>();
        response.setBo(xRecordLoanService.getRecordLoan(gid, userGid));
        return response;
    }

    @GetMapping("loanRecord/list")
    @ApiOperation(value = "借款列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query",
                    defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query",
                    required = true)})
    @LogAnnotation(module = "借款列表")
    public Response<PageTableResponse<Long>> list(@RequestParam(required = false, defaultValue = "0") int limit,
                                                  @RequestParam(required = false, defaultValue = "1") int num, @RequestParam String userGid,
                                                  HttpServletRequest request) {
        Response<PageTableResponse<Long>> response = new Response<>();
        PageTableResponse<Long> pageTableResponse = xRecordLoanService.borrowList(limit, num, userGid);
        pageTableResponse.setAddition(xRecordLoanService.getSumLoanAmount(userGid));
        response.setBo(pageTableResponse);
        return response;
    }

    @Autowired
    private XAPIService xapiService;

    @Autowired
    private I18nService i18nService;

    @GetMapping("enoughBalance")
    @ApiOperation("银行余额是否足够")
    public Response<Boolean> enoughBalance() {
        long result = xapiService.getCanborrowBalance();
        if (result < 0) {
            throw new BusinessException(i18nService.getMessage("response.error.borrow.notEnoughBalance.code"),
                    i18nService.getMessage("response.error.borrow.notEnoughBalance.msg"));
        }
        Response<Boolean> response = new Response<>();
        response.setBo(result >= SysVariable.LOAN_MIN_AMOUNT);
        return response;
    }


}
