package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.dto.Response;
import com.ivay.ivay_manage.service.XLoanRateService;
import com.ivay.ivay_manage.service.XUserInfoService;
import com.ivay.ivay_repository.model.XAuditDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("audit")
@Api(tags = "审核/风控系统")
public class XAuditController {
    @Autowired
    private XUserInfoService xUserInfoService;

    @Autowired
    private XLoanRateService xLoanRateService;

    @PostMapping("list")
    @ApiOperation(value = "审核记录")
    public PageTableResponse auditList(PageTableRequest request) {
        return xUserInfoService.auditList(request);
    }

    @GetMapping("detail")
    @ApiOperation(value = "审核详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    public Response<XAuditDetail> detail(@RequestParam String userGid) {
        Response<XAuditDetail> response = new Response<>();
        response.setBo(xUserInfoService.auditDetail(userGid));
        return response;
    }

    @PostMapping("update")
    @ApiOperation(value = "提交审核结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "0拒绝 1通过 2 重新提交", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "refuseCode", value = "驳回理由code", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "refuseDemo", value = "驳回理由msg", dataType = "String", paramType = "query", required = false)
    })
    public Response<Integer> update(@RequestParam String userGid,
                                    @RequestParam int flag,
                                    @RequestParam(required = false) String refuseCode,
                                    @RequestParam(required = false) String refuseDemo) {
        Response<Integer> response = new Response<>();
        response.setBo(xUserInfoService.auditUpdate(userGid, flag, refuseCode, refuseDemo));
        return response;
    }

    @GetMapping("queryAuditQualification")
    @ApiOperation(value = "查询贷款权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "0 授信 1借款", dataType = "Long", paramType = "query", defaultValue = "0")
    })
    public boolean queryAuditQualification(@RequestParam String userGid,
                                           @RequestParam int flag) {
        return xUserInfoService.queryAuditQualification(userGid, flag);
    }

    @PostMapping("updateCreditLimit")
    @ApiOperation(value = "提額")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    public long updateCreditLimit(@RequestParam String userGid) {
        return xLoanRateService.acquireCreditLimit(userGid);
    }

    @PostMapping("autoAudit")
    @ApiOperation(value = "自动审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    public Response<Boolean> autoAudit(@RequestParam String userGid) {
        Response<Boolean> response = new Response<>();
        response.setBo(xUserInfoService.autoAudit(userGid));
        return response;
    }
}
