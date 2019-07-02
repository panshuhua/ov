package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.CustomerService;
import com.ivay.ivay_manage.service.XUserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("manage/loan")
@Api(tags = "借还款相关")
public class XLoanController {
    @Autowired
    private XUserInfoService xUserInfoService;

    @PostMapping("overDueUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "type", value = "type 0 逾期天数为(0,3], 1 逾期天数为 3天以上", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "identityCard", value = "身份证", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "phone", value = "电话号码", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "name", value = "用户姓名", dataType = "String", paramType = "query", required = false)
    })
    @ApiOperation("查看逾期用户")
    public Response<PageTableResponse> overDueUsers(@RequestParam(required = false, defaultValue = "0") int limit,
                                                    @RequestParam(required = false, defaultValue = "1") int num,
                                                    @RequestParam(required = false) String type,
                                                    @RequestParam(required = false) String identityCard,
                                                    @RequestParam(required = false) String phone,
                                                    @RequestParam(required = false) String name) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xUserInfoService.overDueUsers(limit, num, type, identityCard, phone, name));
        return response;
    }

    @PostMapping("overDueLoan")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "type 0 逾期天数为(0,3], 1 逾期天数为 3天以上", dataType = "String", paramType = "query", required = false)
    })
    @ApiOperation("查看逾期借款信息")
    public Response<PageTableResponse> overDueUsers(@RequestParam(required = false, defaultValue = "0") int limit,
                                                    @RequestParam(required = false, defaultValue = "1") int num,
                                                    @RequestParam String userGid,
                                                    @RequestParam(required = false) String type) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xUserInfoService.overDueLoan(limit, num, userGid, type));
        return response;
    }

    @Autowired
    private CustomerService customerService;

    @GetMapping("listContactInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query")
    })
    @ApiOperation("联系人列表")
    public Response<PageTableResponse> listContactInfo(@RequestParam(required = false, defaultValue = "0") int limit,
                                                       @RequestParam(required = false, defaultValue = "1") int num,
                                                       @RequestParam String userGid) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(customerService.list(limit, num, userGid));
        return response;
    }

    @PostMapping("repaymentInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "type 0 逾期天数为(0,3), 1 逾期天数为[3,)  null全部逾期", dataType = "String", paramType = "query", required = false)
    })
    @ApiOperation("查看还款信息")
    public Response<PageTableResponse> repaymentInfo(@RequestParam(required = false, defaultValue = "0") int limit,
                                                     @RequestParam(required = false, defaultValue = "1") int num,
                                                     @RequestParam String userGid,
                                                     @RequestParam(required = false) String type) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(customerService.repaymentInfo(limit, num, userGid, type));
        return response;
    }
}
