package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XAuditService;
import com.ivay.ivay_manage.service.XUserInfoService;
import com.ivay.ivay_repository.dto.XAuditListInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
            @ApiImplicitParam(name = "id", value = "审计员id", dataType = "Long", paramType = "query")
    })
    public void deleteAudit(@RequestParam Long id) {
        xAuditService.deleteAudit(id);
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "auditName", value = "审计员名字", dataType = "String", paramType = "query")
    })
    @ApiOperation("获得所有的审计员")
    public Response<PageTableResponse> listAuditV2(@RequestParam(required = false, defaultValue = "0") int limit,
                                                   @RequestParam(required = false, defaultValue = "1") int num,
                                                   @RequestParam(required = false) String auditName) {
        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset(limit * (num - 1));
        if (!StringUtils.isEmpty(auditName)) {
            request.getParams().put("username", auditName);
        }
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xAuditService.listAudit(request));
        return response;
    }

    @GetMapping("listUser")
    @ApiOperation("获取某审计员可审计名单")
    public PageTableResponse listUser(PageTableRequest request) {
        return xAuditService.listUser(request);
    }

    @Autowired
    private XUserInfoService xUserInfoService;

    @GetMapping("listUser/v2")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "auditId", value = "审计员id", dataType = "Long", paramType = "query")
    })
    @ApiOperation("获取某审计员可审计名单")
    public Response<PageTableResponse> listUserV2(@RequestParam(required = false, defaultValue = "0") int limit,
                                                  @RequestParam(required = false, defaultValue = "1") int num,
                                                  @RequestParam Integer auditId) {
        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset(limit * (num - 1));
        XAuditListInfo xAuditListInfo = new XAuditListInfo();
        xAuditListInfo.setAuditId(auditId);
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xUserInfoService.auditList(limit, num, xAuditListInfo));
        return response;
    }
}
