package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionCalculateService;
import com.ivay.ivay_repository.model.XCollectionCalculate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("xCollectionCalculates")
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
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<XCollectionCalculate> get(@RequestParam Long id) {
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

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<Integer> delete(@RequestParam Long id) {
        Response<Integer> response = new Response<>();
        response.setBo(xCollectionCalculateService.delete(id));
        return response;
    }
}
