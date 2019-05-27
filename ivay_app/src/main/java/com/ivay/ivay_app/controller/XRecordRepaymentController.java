package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.dto.Response;
import com.ivay.ivay_app.model.XRecordRepayment;
import com.ivay.ivay_app.model.XVirtualAccount;
import com.ivay.ivay_app.service.XRecordRepaymentService;
import com.ivay.ivay_app.table.PageTableResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("star/xRecordRepayments")
@Api(tags = "还款")
public class XRecordRepaymentController {

    @Autowired
    private XRecordRepaymentService xRecordRepaymentService;
    @Autowired
    private I18nService i18nService;

    @PostMapping("update")
    @ApiOperation(value = "提交还款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderGid", value = "借款gid", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "bankShortName", value = "银行名简称", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "repaymentAmount", value = "还款金额", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "deductType", value = "还款类型 0:银行卡", dataType = "Long", paramType = "query", defaultValue = "0", required = true)
    })
    public Response<XVirtualAccount> update(@RequestParam String orderGid,
                                            @RequestParam String userGid,
                                            @RequestParam(required = false) String bankShortName,
                                            @RequestParam long repaymentAmount,
                                            @RequestParam Integer deductType) {
        Response<XVirtualAccount> response = new Response<>();
        XVirtualAccount xVirtualAccount = xRecordRepaymentService.repaymentMoney(orderGid, userGid, bankShortName, repaymentAmount, deductType);

        if (xVirtualAccount != null) {
            if (!"200".equals(xVirtualAccount.getResponseCode())) {
                response.setStatus(xVirtualAccount.getResponseCode(),
                        "调用第三方接口创建/更新虚拟账号失败，返回错误信息为：" + xVirtualAccount.getResponseMessage());
            }
        } else {
            response.setStatus(i18nService.getMessage("response.error.create.virtualAccount.code"),
                    i18nService.getMessage("response.error.create.virtualAccount.msg"));
        }
        response.setBo(xVirtualAccount);
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "获取还款记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "repaymentGid", value = "还款gid", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    public Response<XRecordRepayment> get(@RequestParam String repaymentGid,
                                          @RequestParam String userGid) {
        Response<XRecordRepayment> response = new Response<>();
        response.setBo(xRecordRepaymentService.getByGid(repaymentGid, userGid));
        return response;
    }

    @GetMapping("list")
    @ApiOperation(value = "还款列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    public Response<PageTableResponse> list(@RequestParam(required = false, defaultValue = "0") int limit,
                                            @RequestParam(required = false, defaultValue = "1") int num,
                                            @RequestParam String userGid) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xRecordRepaymentService.list(limit, num, userGid));
        return response;
    }
}
