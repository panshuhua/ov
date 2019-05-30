package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.Response;
import com.ivay.ivay_repository.model.XConfig;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_app.service.XConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "配置文件")
@RestController
@RequestMapping("star/config")
public class XConfigController {
    private static final Logger logger = LoggerFactory.getLogger(XConfigController.class);
    @Autowired
    private XConfigService xConfigService;

    @PostMapping("update")
    @ApiOperation(value = "编辑配置文件")
    public Response<XConfig> update(@RequestBody XConfig xConfig) {
        Response<XConfig> response = new Response<>();
        response.setBo(xConfigService.update(xConfig));
        return response;
    }

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
        logger.info("此处有一个info信息");
        logger.warn("此处有一个warn信息");
        logger.error("此处有一个error信息");
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xConfigService.list(limit, num));
        System.out.println("执行结束");
        return response;
    }
}
