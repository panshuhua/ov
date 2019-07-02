package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.RedisUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.service.XLoanService;
import com.ivay.ivay_manage.service.XUserInfoService;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dto.XAuditDetail;
import com.ivay.ivay_repository.dto.XAuditListInfo;
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

    @PostMapping("list")
    @ApiOperation("审核记录")
    public PageTableResponse auditList(PageTableRequest request) {
        return xUserInfoService.auditList(request);
    }

    @PostMapping("list/v2")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1")
    })
    @ApiOperation("审核记录")
    public Response<PageTableResponse> auditListNew(@RequestParam(required = false, defaultValue = "0") int limit,
                                                    @RequestParam(required = false, defaultValue = "1") int num,
                                                    @RequestBody(required = false) XAuditListInfo xAuditListInfo) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xUserInfoService.auditList(limit,num,xAuditListInfo));
        return response;
    }

    @GetMapping("detail")
    @ApiOperation("审核详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    public Response<XAuditDetail> detail(@RequestParam String userGid) {
        Response<XAuditDetail> response = new Response<>();
        response.setBo(xUserInfoService.auditDetail(userGid));
        return response;
    }

    @PostMapping("update")
    @ApiOperation("对待授信用户进行人工审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "0拒绝 1通过", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "refuseCode", value = "驳回理由code", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "refuseDemo", value = "驳回理由msg", dataType = "String", paramType = "query", required = false)
    })
    public Response<Integer> update(@RequestParam String userGid,
                                    @RequestParam int flag,
                                    @RequestParam(required = false) String refuseCode,
                                    @RequestParam(required = false) String refuseDemo) {
        Response<Integer> response = new Response<>();
        response.setBo(xUserInfoService.auditUpdate(userGid, flag, refuseCode, refuseDemo, SysVariable.AUDIT_REFUSE_TYPE_MANUAL));
        return response;
    }

    @Autowired
    private XLoanService xLoanService;

    @GetMapping("queryAuditQualification")
    @ApiOperation("查询贷款权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "0 授信 1借款", dataType = "Long", paramType = "query", defaultValue = "0")
    })
    public String queryAuditQualification(@RequestParam String userGid,
                                          @RequestParam int flag) {
        // 获得某人的风控审核结果，返回未通过审核的理由，空字符串表示通过审核
        return xLoanService.queryRiskQualificationDemo(userGid, flag);
    }

    @PostMapping("repaymentSuccessPostHandle")
    @ApiOperation("还款成功的后置处理: 包括提额、增加白名单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    public long repaymentSuccessPostHandle(@RequestParam String userGid) {
        return xLoanService.repaymentSuccessPostHandle(userGid);
    }

    @PostMapping("autoAudit")
    @ApiOperation("对待授信用户进行自动审核或分配审计员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    public Response<Boolean> autoAudit(@RequestParam String userGid) {
        Response<Boolean> response = new Response<>();
        response.setBo(xUserInfoService.autoAudit(userGid));
        return response;
    }

    @Autowired
    private XUserInfoDao xUserInfoDao;

    @PostMapping("riskRefuseList")
    @ApiOperation("被风控规则拒绝得名单")
    public PageTableResponse riskRefuseList(PageTableRequest request) {
        request.getParams().put("refuseType", SysVariable.AUDIT_REFUSE_TYPE_AUTO);
        request.getParams().put("orderBy", null);
        List<XUserInfo> list = xUserInfoDao.list(request.getParams(), request.getOffset(), request.getLimit());
        return new PageTableResponse(list.size(), request.getOffset(), list);
    }

    @GetMapping("listSameName")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    @ApiOperation("获取与某用户同名得所有用户")
    public Response<PageTableResponse> listSameName(@RequestParam(required = false, defaultValue = "0") int limit,
                                                    @RequestParam(required = false, defaultValue = "1") int num,
                                                    @RequestParam String userGid) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xUserInfoService.listSameName(limit, num, userGid));
        return response;
    }

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("test")
    public boolean test(String key) {
        redisUtils.set(key, "test redis");
        return true;
    }
}
