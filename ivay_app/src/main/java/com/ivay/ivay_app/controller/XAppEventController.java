package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.service.XAppEventService;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_repository.model.XAppEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("star/xAppEvents")
@Api(tags = "app事件上报")
public class XAppEventController {

    @Autowired
    private XAppEventService xAppEventService;

    @PostMapping("save")
    @ApiOperation(value = "插入待上报谷歌的app事件")
    public Response<XAppEvent> save(@RequestBody XAppEvent xAppEvent) {
        Response<XAppEvent> response = new Response<>();
        response.setBo(xAppEventService.save(xAppEvent));
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "查询所有用户的待上传事件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    public Response<List<XAppEvent>> get(@RequestParam String userGid) {
        Response<List<XAppEvent>> response = new Response<>();
        response.setBo(xAppEventService.listToBeUpload(userGid));
        return response;
    }

    @PostMapping("delete")
    @ApiOperation(value = "上报事件后删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gids", value = "用户gid或借款订单id", dataType = "String", paramType = "query")
    })
    public Response<Integer> delete(@RequestParam String gids) {
        Response<Integer> response = new Response<>();
        response.setBo(xAppEventService.delete(gids));
        return response;
    }
}
