package com.ivay.ivay_manage.controller;

import com.alibaba.fastjson.JSONObject;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.RiskUserService;
import com.ivay.ivay_manage.service.UserService;
import com.ivay.ivay_repository.dto.RiskUserInfo;
import com.ivay.ivay_repository.dto.UserName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RiskUserController
 * @Description 白名单
 * @Author Ryan
 * @Date 2019/7/23 9:11
 */
@RestController
@RequestMapping("api/riskUser")
@Api(tags = "白名单管理")
public class RiskUserController {

    @Autowired
    private RiskUserService riskUserService;
    @Autowired
    private UserService userService;

    @PostMapping("list")
    @ApiOperation(value = "白名单搜索列表")
    public Response<PageTableResponse> list(@RequestParam(required = false, defaultValue = "0") int limit,
                                            @RequestParam(required = false, defaultValue = "1") int num,
                                            @RequestBody(required = false) RiskUserInfo riskUserInfo) {

        Response<PageTableResponse> response = new Response<>();
        response.setBo(riskUserService.list(limit, num, riskUserInfo));
        return response;
    }

    @GetMapping("salesmanList")
    @ApiOperation(value = "销售员下拉列表")
    public List<UserName> salesList() {

        return userService.getSalesmanNames();
    }

    @GetMapping("updateSalesman")
    @ApiOperation(value = "指派销售员")
    public Response<Boolean> updateSalesman(@RequestParam(required = true) Integer salesmanId,
                                            @RequestParam(required = true) String ids) {

        if (StringUtils.isBlank(ids)) {
            throw new BusinessException("参数不能为空");
        }
        Response<Boolean> response = new Response<>();

        List<Integer> list = new ArrayList<>();
        for (String strId : ids.split(",")) {
            try {
                list.add(Integer.parseInt(strId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.setBo(riskUserService.updateSalesman(salesmanId, list));
        return response;
    }

    @PostMapping("mySalesList")
    @ApiOperation(value = "我的电销列表")
    public Response<PageTableResponse> mySalesList(@RequestParam(required = false, defaultValue = "0") int limit,
                                            @RequestParam(required = false, defaultValue = "1") int num,
                                            @RequestBody(required = false) RiskUserInfo riskUserInfo) {

        Response<PageTableResponse> response = new Response<>();
        response.setBo(riskUserService.mySalesList(limit, num, riskUserInfo));
        return response;
    }

    @GetMapping("updateSalesmanAuto")
    @ApiOperation(value = "触发自动分配电销接口")
    public Response<Boolean> updateSalesmanAuto() {

        Response<Boolean> response = new Response<>();
        response.setBo(riskUserService.updateSalesmanAuto());
        return response;
    }
}
