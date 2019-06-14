package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.dto.Response;
import com.ivay.ivay_manage.service.XLoanRateService;
import com.ivay.ivay_manage.service.XUserInfoService;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.XAuditDetail;
import com.ivay.ivay_repository.model.XUserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    @LogAnnotation(module = "审核记录")
    public PageTableResponse auditList(PageTableRequest request) {
        return xUserInfoService.auditList(request);
    }

    @GetMapping("detail")
    @ApiOperation(value = "审核详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    @LogAnnotation(module = "审核详情")
    public Response<XAuditDetail> detail(@RequestParam String userGid) {
        Response<XAuditDetail> response = new Response<>();
        response.setBo(xUserInfoService.auditDetail(userGid));
        return response;
    }

    @PostMapping("update")
    @ApiOperation(value = "提交审核结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "0拒绝 1通过", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "refuseCode", value = "驳回理由code", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "refuseDemo", value = "驳回理由msg", dataType = "String", paramType = "query", required = false)
    })
    @LogAnnotation(module = "提交审核结果")
    public Response<Integer> update(@RequestParam String userGid,
                                    @RequestParam int flag,
                                    @RequestParam(required = false) String refuseCode,
                                    @RequestParam(required = false) String refuseDemo) {
        Response<Integer> response = new Response<>();
        response.setBo(xUserInfoService.auditUpdate(userGid, flag, refuseCode, refuseDemo, SysVariable.AUDIT_REFUSE_TYPE_MANUAL));
        return response;
    }

    @GetMapping("queryAuditQualification")
    @ApiOperation(value = "查询贷款权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "0 授信 1借款", dataType = "Long", paramType = "query", defaultValue = "0")
    })
    @LogAnnotation(module = "查询贷款权限")
    public String queryAuditQualification(@RequestParam String userGid,
                                          @RequestParam int flag) {
        // 获得某人的风控审核结果，返回未通过审核的理由，空字符串表示通过审核
        return xUserInfoService.queryRiskQualificationDemo(userGid, flag);
    }

    @PostMapping("updateCreditLimit")
    @ApiOperation(value = "提額")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    @LogAnnotation(module = "提额")
    public long updateCreditLimit(@RequestParam String userGid) {
        return xLoanRateService.acquireCreditLimit(userGid);
    }

    @PostMapping("autoAudit")
    @ApiOperation(value = "自动审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    @LogAnnotation(module = "自动审核")
    public Response<Boolean> autoAudit(@RequestParam String userGid) {
        Response<Boolean> response = new Response<>();
        response.setBo(xUserInfoService.autoAudit(userGid));
        return response;
    }


    @Autowired
    private XUserInfoDao xUserInfoDao;

    @PostMapping("riskRefuseList")
    @ApiOperation(value = "被风控规则拒绝得名单")
    @LogAnnotation(module = "被风控规则拒绝得名单")
    public PageTableResponse riskRefuseList(PageTableRequest request) {
        request.getParams().put("refuseType", SysVariable.AUDIT_REFUSE_TYPE_AUTO);
        request.getParams().put("orderBy", null);
        List<XUserInfo> list = xUserInfoDao.list(request.getParams(), request.getOffset(), request.getLimit());
        return new PageTableResponse(list.size(), request.getOffset(), list);
    }

}
