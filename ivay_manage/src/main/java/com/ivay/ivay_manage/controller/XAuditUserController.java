package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.dto.Response;
import com.ivay.ivay_manage.service.XAuditUserService;
import com.ivay.ivay_repository.model.XAuditUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("manage/xAuditUsers")
@Api(tags = "审计人员分配接口")
public class XAuditUserController {

    @Autowired
    private XAuditUserService xAuditUserService;

    @PutMapping("update")
    @ApiOperation(value = "为某个用户分配一个审计员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "auditId", value = "审计员id,不设置则随机分配", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    public Response<XAuditUser> update(@RequestParam(required = false) String auditId, @RequestParam String userGid) {
        Response<XAuditUser> response = new Response<>();
        response.setBo(xAuditUserService.update(auditId, userGid));
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "获取某审计员可审计名单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "auditId", value = "审计员id", dataType = "String", paramType = "query")
    })
    public Response<List<XAuditUser>> get(@RequestParam String auditId) {
        Response<List<XAuditUser>> response = new Response<>();
        response.setBo(xAuditUserService.getBySysUserId(auditId));
        return response;
    }

    @DeleteMapping("deleteAudit")
    @ApiOperation(value = "删除某个审计员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "审计员id，可批量处理", dataType = "String", paramType = "query")
    })
    public void deleteAudit(@RequestParam String ids) {
        xAuditUserService.deleteAudit(ids);
    }

    @DeleteMapping("deleteUser")
    @ApiOperation(value = "删除被某个审计员审计的用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "用户gid，可批量处理", dataType = "String", paramType = "query")
    })
    public void deleteUser(@RequestParam String ids) {
        xAuditUserService.deleteUser(ids);
    }

    @GetMapping("list")
    @ApiOperation(value = "获得所有的审计员")
    public PageTableResponse list(PageTableRequest request) {
        return xAuditUserService.list(request);
    }


}
