package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.Response;
import com.ivay.ivay_app.service.XUserExtInfoService;
import com.ivay.ivay_repository.model.XUserExtInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "授信-扩展信息")
@RestController
@RequestMapping("star/userExtInfos")
public class XUserExtInfoController {
    @Autowired
    private XUserExtInfoService xUserExtInfoService;

    @GetMapping("get")
    @ApiOperation(value = "获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    public Response<XUserExtInfo> get(@RequestParam String userGid) {
        Response<XUserExtInfo> response = new Response<>();
        response.setBo(xUserExtInfoService.getByGid(userGid));
        return response;
    }

    @PostMapping("save")
    @ApiOperation(value = "保存")
    public Response<XUserExtInfo> update(@RequestBody XUserExtInfo xUserExtInfo) {
        Response<XUserExtInfo> response = new Response<>();
        response.setBo(xUserExtInfoService.update(xUserExtInfo));
        return response;
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    public void delete(@RequestParam String userGid) {
        xUserExtInfoService.delete(userGid);
    }
}
