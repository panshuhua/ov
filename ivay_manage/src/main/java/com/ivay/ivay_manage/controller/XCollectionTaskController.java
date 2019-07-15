package com.ivay.ivay_manage.controller;

import com.alibaba.fastjson.JSONObject;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.UserService;
import com.ivay.ivay_manage.service.XCollectionTaskService;
import com.ivay.ivay_repository.dto.CollectionTaskInfo;
import com.ivay.ivay_repository.dto.UserName;
import com.ivay.ivay_repository.model.XCollectionTask;
import com.ivay.ivay_repository.model.XRecordLoan;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
                                            @RequestBody(required = false) CollectionTaskInfo collectionTaskInfo) {

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

    @GetMapping(value = "updateCollector")
    @ApiOperation(value = "指派催收人")
    public Response<Boolean> updateCollector(@RequestParam Integer collectorId,
                                             @RequestParam(required = true) String ids) {
        if(StringUtils.isBlank(ids)){
            throw new BusinessException("参数不能为空！");
        }
        String[] stringIds = ids.split(",");
        List<Integer> list = new ArrayList<>();
        for (String strId : stringIds) {
            try{
                list.add(Integer.parseInt(strId));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Response<Boolean> response = new Response<>();
        response.setBo(xCollectionTaskService.updateCollector(collectorId, list));

        return response;
    }

    @ApiOperation(value = "获取用户名字列表")
    @GetMapping("nameList")
    public List<UserName> getUserNames() {
        return userService.getUserNames();
    }


    @ApiOperation(value = "我的催收")
    @PostMapping("myCollections")
    public Response<PageTableResponse> getCollectionListByUserGid(@RequestParam(required = false, defaultValue = "0") int limit,
                                                                  @RequestParam(required = false, defaultValue = "1") int num,
                                                                  @RequestBody(required = false) CollectionTaskInfo collectionTaskInfo) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xCollectionTaskService.getCollectionListByUserGid(limit, num, collectionTaskInfo));
        return response;
    }

    @ApiOperation(value = "催收回款")
    @PostMapping("collectionsRepay")
    public Response<PageTableResponse> getCollectionsRepayList(@RequestParam(required = false, defaultValue = "0") int limit,
                                                               @RequestParam(required = false, defaultValue = "1") int num,
                                                               @RequestBody(required = false) CollectionTaskInfo collectionTaskInfo) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xCollectionTaskService.getCollectionsRepayList(limit, num, collectionTaskInfo));
        return response;
    }

    @GetMapping("loanOrderInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "档案id", dataType = "Long", paramType = "query")
    })
    @ApiOperation("借款订单详情")
    public Response<XRecordLoan> loanOrderInfo(@RequestParam long taskId) {
        Response<XRecordLoan> response = new Response<>();
        response.setBo(xCollectionTaskService.loanOrderInfo(taskId));
        return response;
    }

    @GetMapping("repaymentInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "档案id", dataType = "Long", paramType = "query")
    })
    @ApiOperation("还款详情")
    public Response<PageTableResponse> repaymentInfo(@RequestParam long taskId) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xCollectionTaskService.repaymentInfo(taskId));
        return response;
    }

    @GetMapping("overdueOrder")
    @ApiOperation("手动触发定时任务")
    public Response<Boolean> executeOverdueOrderJob(@RequestParam long taskId) {
        xCollectionTaskService.saveCollectionTaskBatch();
        Response<Boolean> response = new Response<>();
        return response;
    }
}
