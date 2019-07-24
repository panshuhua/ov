package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XSalesRecordService;
import com.ivay.ivay_repository.model.XSalesRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/salesRecord")
@Api(tags = "销售记录")
public class XSalesRecordController {

    @Autowired
    private XSalesRecordService xSalesRecordService;

    @PostMapping("add")
    @ApiOperation(value = "添加销售记录")
    public Response<Boolean> add(@RequestBody XSalesRecord xSalesRecord) {
        Response<Boolean> response = new Response<>();
        response.setBo(xSalesRecordService.add(xSalesRecord));
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "根据id获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<XSalesRecord> get(@RequestParam Long id) {
        Response<XSalesRecord> response = new Response<>();
        response.setBo(xSalesRecordService.get(id));
        return response;
    }

    @PutMapping("update")
    @ApiOperation(value = "修改")
    public Response<XSalesRecord> update(@RequestBody XSalesRecord xSalesRecord) {
        Response<XSalesRecord> response = new Response<>();
        response.setBo(xSalesRecordService.update(xSalesRecord));
        return response;
    }

    @GetMapping("list")
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {
        return xSalesRecordService.list(request);
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<Integer> delete(@RequestParam Long id) {
        Response<Integer> response = new Response<>();
        response.setBo(xSalesRecordService.delete(id));
        return response;
    }


    @GetMapping("salesRecordList")
    @ApiOperation(value = "电销记录")
    public Response<PageTableResponse> getSalesRecordList(@RequestParam(required = false, defaultValue = "0") int limit,
                                                   @RequestParam(required = false, defaultValue = "1") int num,
                                                   @RequestParam(required = true) int id) {

        Response<PageTableResponse> response = new Response<>();
        response.setBo(xSalesRecordService.getSalesRecordList(limit, num, id));
        return response;
    }
}
