package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionRecordService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.model.XCollectionRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/xCollectionRecords")
@Api(tags = "催收记录")
public class XCollectionRecordController {

    @Autowired
    private XCollectionRecordService xCollectionRecordService;

    @PostMapping("save")
    @ApiOperation(value = "保存")
    public Response<Boolean> save(@RequestBody XCollectionRecord xCollectionRecord) {
        Response<Boolean> response = new Response<>();
        xCollectionRecord.setCollectorId(UserUtil.getLoginUser().getId().intValue());
        response.setBo(xCollectionRecordService.save(xCollectionRecord) >= 1);
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "根据id获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<XCollectionRecord> get(@RequestParam Long id) {
        Response<XCollectionRecord> response = new Response<>();
        response.setBo(xCollectionRecordService.get(id));
        return response;
    }

    @PutMapping("update")
    @ApiOperation(value = "修改")
    public Response<XCollectionRecord> update(@RequestBody XCollectionRecord xCollectionRecord) {
        Response<XCollectionRecord> response = new Response<>();
        response.setBo(xCollectionRecordService.update(xCollectionRecord));
        return response;
    }

    @GetMapping("list")
    @ApiOperation(value = "列表")
    public Response<PageTableResponse> selectCollectionRecordList(@RequestParam(required = false, defaultValue = "0") int limit,
                                                                  @RequestParam(required = false, defaultValue = "1") int num,
                                                                  @RequestParam int id) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xCollectionRecordService.selectCollectionRecordList(limit,num,id));
        return response;
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<Integer> delete(@RequestParam Long id) {
        Response<Integer> response = new Response<>();
        response.setBo(xCollectionRecordService.delete(id));
        return response;
    }
}
