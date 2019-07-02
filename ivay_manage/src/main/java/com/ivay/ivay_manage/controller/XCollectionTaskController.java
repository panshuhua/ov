package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionTaskService;
import com.ivay.ivay_repository.model.XCollectionTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("xCollectionTasks")
@Api(tags = "催收派单")
public class XCollectionTaskController {

    @Autowired
    private XCollectionTaskService xCollectionTaskService;

    @PostMapping("save")
    @ApiOperation(value = "保存")
    public Response<XCollectionTask> save(@RequestBody XCollectionTask xCollectionTask) {
        Response<XCollectionTask> response = new Response<>();
        response.setBo(xCollectionTaskService.save(xCollectionTask));
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "根据id获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<XCollectionTask> get(@RequestParam Long id) {
        Response<XCollectionTask> response = new Response<>();
        response.setBo(xCollectionTaskService.get(id));
        return response;
    }

    @PutMapping("update")
    @ApiOperation(value = "修改")
    public Response<XCollectionTask> update(@RequestBody XCollectionTask xCollectionTask) {
        Response<XCollectionTask> response = new Response<>();
        response.setBo(xCollectionTaskService.update(xCollectionTask));
        return response;
    }

    @GetMapping("list")
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {
        return xCollectionTaskService.list(request);
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<Integer> delete(@RequestParam Long id) {
        Response<Integer> response = new Response<>();
        response.setBo(xCollectionTaskService.delete(id));
        return response;
    }
}
