package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.UserService;
import com.ivay.ivay_manage.service.XCollectionTaskService;
import com.ivay.ivay_repository.dto.CollectionTaskInfo;
import com.ivay.ivay_repository.dto.UserName;
import com.ivay.ivay_repository.model.SysUser;
import com.ivay.ivay_repository.model.XCollectionTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/xCollectionTasks")
@Api(tags = "催收派单")
public class XCollectionTaskController {

    @Autowired
    private XCollectionTaskService xCollectionTaskService;
    @Autowired
    private UserService userService;

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
            @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query")
    })
    public Response<XCollectionTask> get(@RequestParam Integer id) {
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
    @ApiOperation(value = "催收搜索列表")
    public Response<PageTableResponse> list(@RequestParam(required = false, defaultValue = "0") int limit,
                                  @RequestParam(required = false, defaultValue = "1") int num,
                                  PageTableRequest request, CollectionTaskInfo collectionTaskInfo) {

        Response<PageTableResponse> response = new Response<>();
        response.setBo(xCollectionTaskService.list(limit, num, collectionTaskInfo));
        return response;
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

    @GetMapping("updateCollector")
    @ApiOperation(value = "指派催收人")
    public Response<Boolean> updateCollector(@RequestParam(required = true) Integer collectorId,
                                    @RequestParam(required = true) Integer id) {
        Response<Boolean> response = new Response<>();
        response.setBo(xCollectionTaskService.updateCollector(collectorId, id));

        return response;
    }

    @ApiOperation(value = "获取用户名字列表")
    @GetMapping("nameList")
    public List<UserName> getUserNames() {
        return userService.getUserNames();
    }


    @ApiOperation(value = "我的催收")
    @GetMapping("myCollections")
    public Response<PageTableResponse> getCollectionListByUserGid(@RequestParam(required = false, defaultValue = "0") int limit,
                                                                  @RequestParam(required = false, defaultValue = "1") int num) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xCollectionTaskService.getCollectionListByUserGid(limit, num));
        return response;
    }

    @ApiOperation(value = "催收回款")
    @GetMapping("collectionsRepay")
    public Response<PageTableResponse> getCollectionsRepayList(@RequestParam(required = false, defaultValue = "0") int limit,
                                                               @RequestParam(required = false, defaultValue = "1") int num) {
        Response<PageTableResponse> response = new Response<>();
        //response.setBo(xCollectionTaskService.list(limit, num));
        return response;
    }
}
