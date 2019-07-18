package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionCalculateService;
import com.ivay.ivay_repository.dto.CollectionCalculateInfo;
import com.ivay.ivay_repository.model.XCollectionCalculate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/collectionCalculates")
@Api(tags = "XCollectionCalculateController")
public class XCollectionCalculateController {

    @Autowired
    private XCollectionCalculateService xCollectionCalculateService;

    @PostMapping("save")
    @ApiOperation(value = "保存")
    public Response<XCollectionCalculate> save(@RequestBody XCollectionCalculate xCollectionCalculate) {
        Response<XCollectionCalculate> response = new Response<>();
        response.setBo(xCollectionCalculateService.save(xCollectionCalculate));
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "根据id获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query")
    })
    public Response<XCollectionCalculate> get(@RequestParam Integer id) {
        Response<XCollectionCalculate> response = new Response<>();
        response.setBo(xCollectionCalculateService.get(id));
        return response;
    }

    @PutMapping("update")
    @ApiOperation(value = "修改")
    public Response<XCollectionCalculate> update(@RequestBody XCollectionCalculate xCollectionCalculate) {
        Response<XCollectionCalculate> response = new Response<>();
        response.setBo(xCollectionCalculateService.update(xCollectionCalculate));
        return response;
    }

    @GetMapping("list")
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {
        return xCollectionCalculateService.list(request);
    }

    @PostMapping("calculateList")
    @ApiOperation(value = "催收报表统计列表")
    public Response<PageTableResponse> calculateList(@RequestParam(required = false, defaultValue = "0") int limit,
                                                     @RequestParam(required = false, defaultValue = "1") int num,
                                                     @RequestBody(required = false) CollectionCalculateInfo collectionCalculateInfo) {

        Response<PageTableResponse> response = new Response<>();
        response.setBo(xCollectionCalculateService.selectCalculateList(limit, num, collectionCalculateInfo));
        return response;
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query")
    })
    public Response<Integer> delete(@RequestParam Integer id) {
        Response<Integer> response = new Response<>();
        response.setBo(xCollectionCalculateService.delete(id));
        return response;
    }

    @GetMapping("executeTask")
    @ApiOperation(value = "手动触发催收报表定时任务")
    public Response<Boolean> executeTask(PageTableRequest request) {
        xCollectionCalculateService.saveCollectionCalculate(null);
        Response<Boolean> response = new Response<>();
        return response;
    }

    @GetMapping("repayList")
    @ApiOperation(value = "还款名单")
    public Response<PageTableResponse> repayList(@RequestParam(required = false, defaultValue = "0") int limit,
                                                 @RequestParam(required = false, defaultValue = "1") int num,
                                                 @RequestParam(required = true) int id) {

        Response<PageTableResponse> response = new Response<>();
        response.setBo(xCollectionCalculateService.selectRepayList(limit, num, id));
        return response;
    }
}
