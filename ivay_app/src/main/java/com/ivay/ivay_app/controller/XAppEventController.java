package com.ivay.ivay_app.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_app.service.XAppEventService;
import com.ivay.ivay_repository.model.XAppEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("star/xAppEvents")
@Api(tags = "app事件上报")
public class XAppEventController {

    @Autowired
    private XAppEventService xAppEventService;

    @PostMapping("save")
    @ApiOperation(value = "保存")
    public Response<XAppEvent> save(@RequestBody XAppEvent xAppEvent) {
        Response<XAppEvent> response = new Response<>();
        response.setBo(xAppEventService.save(xAppEvent));
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "根据id获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<XAppEvent> get(@RequestParam Long id) {
        Response<XAppEvent> response = new Response<>();
        response.setBo(xAppEventService.get(id));
        return response;
    }

    @PutMapping("update")
    @ApiOperation(value = "修改")
    public Response<XAppEvent> update(@RequestBody XAppEvent xAppEvent) {
        Response<XAppEvent> response = new Response<>();
        response.setBo(xAppEventService.update(xAppEvent));
        return response;
    }

    @GetMapping("list")
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {
        return xAppEventService.list(request);
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<Integer> delete(@RequestParam Long id) {
        Response<Integer> response = new Response<>();
        response.setBo(xAppEventService.delete(id));
        return response;
    }
}
