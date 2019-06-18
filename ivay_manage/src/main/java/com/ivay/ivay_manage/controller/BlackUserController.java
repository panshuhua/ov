package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.BlackUserService;
import com.ivay.ivay_repository.model.BlackUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("manage/blackUsers")
@Api(tags = "黑名单")
public class BlackUserController {
    @Autowired
    private BlackUserService blackUserService;

    @PostMapping("save")
    @ApiOperation(value = "保存")
    public Response<BlackUser> save(@RequestBody BlackUser blackUser) {
        Response<BlackUser> response = new Response<>();
        response.setBo(blackUserService.save(blackUser));
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "根据id获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<BlackUser> get(@RequestParam Long id) {
        Response<BlackUser> response = new Response<>();
        response.setBo(blackUserService.get(id));
        return response;
    }

    @PutMapping("update")
    @ApiOperation(value = "修改")
    public Response<BlackUser> update(@RequestBody BlackUser blackUser) {
        Response<BlackUser> response = new Response<>();
        response.setBo(blackUserService.update(blackUser));
        return response;
    }

    @GetMapping("list")
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {
        return blackUserService.list(request);
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public Response<Integer> delete(@RequestParam Long id) {
        Response<Integer> response = new Response<>();
        response.setBo(blackUserService.delete(id));
        return response;
    }

    @GetMapping("isBlackUser")
    @ApiOperation(value = "判断是否是黑名单用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "电话号码", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "identityCard", value = "身份证", dataType = "String", paramType = "query", required = false)
    })
    public Response<Boolean> isBlackUser(@RequestParam(required = false) String phone,
                                         @RequestParam(required = false) String identityCard) {
        Response<Boolean> response = new Response<>();
        response.setBo(blackUserService.isBlackUser(phone, identityCard));
        return response;
    }


}
