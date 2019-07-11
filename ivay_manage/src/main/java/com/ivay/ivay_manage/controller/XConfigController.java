package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "配置文件")
@RestController
@RequestMapping("star/config")
public class XConfigController {

    @Autowired
    private XConfigService xConfigService;

    @GetMapping("get")
    @ApiOperation(value = "获取配置内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "配置类型", dataType = "String", paramType = "query"),
    })
    public Response<String> get(String type) {
        Response<String> result = new Response<>();
        result.setBo(xConfigService.getContentByType(type));
        return result;
    }

    @GetMapping("list")
    @ApiOperation(value = "配置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1")
    })
    public Response<PageTableResponse> list(@RequestParam(required = false, defaultValue = "0") int limit,
                                            @RequestParam(required = false, defaultValue = "1") int num) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xConfigService.list(limit, num));
        return response;
    }
}
