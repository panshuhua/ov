package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XAuditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("manage/xAuditUsers")
@Api(tags = "审计人员分配接口")
public class XAuditUserController {

    @Autowired
    private XAuditService xAuditService;

    @PostMapping("update")
    @ApiOperation("重新分配审计员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "0将某审计员的全部用户重新分配  1将某个用户重新分配", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "handleId", value = "要处理的审计员id或用户id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "acceptId", value = "被指派的审计员id, 不指定则随机分配", dataType = "String", paramType = "query", required = false)
    })
    public boolean update(@RequestParam String flag,
                          @RequestParam String handleId,
                          @RequestParam(required = false) String acceptId) {
        if ("1".equals(flag)) {
            return xAuditService.assignAuditForUser(acceptId, handleId) != null;
        } else {
            return xAuditService.reAssignAudit(acceptId, handleId);
        }
    }

    @DeleteMapping("deleteAudit")
    @ApiOperation("删除某个审计员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "审计员id，可批量处理", dataType = "String", paramType = "query")
    })
    public void deleteAudit(@RequestParam String ids) {
        xAuditService.deleteAudit(ids);
    }

    @DeleteMapping("deleteUser")
    @ApiOperation("删除被某个审计员审计的用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "用户gid，可批量处理", dataType = "String", paramType = "query")
    })
    public void deleteUser(@RequestParam String ids) {
        xAuditService.deleteUser(ids);
    }

    @GetMapping("listAudit")
    @ApiOperation("获得所有的审计员")
    public PageTableResponse listAudit(PageTableRequest request) {
        request.setLimit(0);
        return xAuditService.listAudit(request);
    }

    @GetMapping("listAudit/v2")
    @ApiOperation("获得所有的审计员")
    public PageTableResponse listAuditV2(PageTableRequest request) {
        request.setLimit(0);
        return xAuditService.listAudit(request);
    }

    @GetMapping("listUser")
    @ApiOperation("获取某审计员可审计名单")
    public PageTableResponse listUser(PageTableRequest request) {
        return xAuditService.listUser(request);
    }
}
