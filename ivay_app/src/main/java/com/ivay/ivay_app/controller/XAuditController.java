package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.Response;
import com.ivay.ivay_app.dto.XAuditCondition;
import com.ivay.ivay_app.dto.XAuditDetail;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_app.service.XUserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("star")
@Api(tags = "审核系统")
public class XAuditController {
    @Autowired
    private XUserInfoService xUserInfoService;

    @PostMapping("audit/list")
    @ApiOperation(value = "审核记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1")
    })
    public Response<PageTableResponse> auditList(@RequestParam(required = false, defaultValue = "0") int limit,
                                                 @RequestParam(required = false, defaultValue = "1") int num,
                                                 @RequestBody XAuditCondition xAuditCondition) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xUserInfoService.auditList(limit, num, xAuditCondition));
        return response;
    }

    @GetMapping("audit/detail")
    @ApiOperation(value = "审核详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    public Response<XAuditDetail> detail(@RequestParam String userGid) {
        Response<XAuditDetail> response = new Response<>();
        response.setBo(xUserInfoService.auditDetail(userGid));
        return response;
    }

    @PostMapping("audit/update")
    @ApiOperation(value = "提交审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "0拒绝 1通过 2 重新提交", dataType = "Long", paramType = "query", defaultValue = "1")
    })
    public Response<Integer> update(@RequestParam String userGid,
                                    @RequestParam int flag) {
        Response<Integer> response = new Response<>();
        response.setBo(xUserInfoService.auditUpdate(userGid, flag));
        return response;
    }
}
