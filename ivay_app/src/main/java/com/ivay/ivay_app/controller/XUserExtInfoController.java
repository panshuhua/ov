package com.ivay.ivay_app.controller;

import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_repository.model.XUserExtInfo;
import com.ivay.ivay_app.service.XUserExtInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

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
    @LogAnnotation(module="获取用户扩展信息")
    public Response<XUserExtInfo> get(@RequestParam String userGid,HttpServletRequest request) {
        Response<XUserExtInfo> response = new Response<>();
        response.setBo(xUserExtInfoService.getByGid(userGid));
        return response;
    }

    @PostMapping("save")
    @ApiOperation(value = "保存")
    @LogAnnotation(module="保存用户扩展信息")
    public Response<XUserExtInfo> update(@RequestBody XUserExtInfo xUserExtInfo,HttpServletRequest request) {
        Response<XUserExtInfo> response = new Response<>();
        response.setBo(xUserExtInfoService.update(xUserExtInfo));
        return response;
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    @LogAnnotation(module="删除用户扩展信息")
    public void delete(@RequestParam String userGid,HttpServletRequest request) {
        xUserExtInfoService.delete(userGid);
    }
}
