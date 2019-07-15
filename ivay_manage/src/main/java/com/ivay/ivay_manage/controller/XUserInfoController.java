package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XUserInfoService;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dao.master.XUserRiskDao;
import com.ivay.ivay_repository.model.XUserInfo;
import com.ivay.ivay_repository.model.XUserRisk;
import com.ivay.ivay_repository.utils.DesensitizationUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("manage/user")
@Api(tags = "用户相关信息")
public class XUserInfoController {
    @Autowired
    private XUserInfoService xUserInfoService;

    @Autowired
    private XUserInfoDao xUserInfoDao;

    @GetMapping("getUserInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    @ApiOperation("获取用户信息")
    public Response<XUserInfo> getUserInfo(@RequestParam String userGid) {
        Response<XUserInfo> response = new Response<>();
        // 隐藏密码
        response.setBo(DesensitizationUtil.UserInfoDesensitization(xUserInfoDao.getByUserGid(userGid)));
        return response;
    }

    @GetMapping("cardAndBankInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    @ApiOperation("银行卡信息")
    public Response<PageTableResponse> cardAndBankInfo(@RequestParam(required = false, defaultValue = "0") int limit,
                                                       @RequestParam(required = false, defaultValue = "1") int num,
                                                       @RequestParam String userGid) {
        Response<PageTableResponse> response = new Response<>();
        response.setBo(xUserInfoService.cardAndBankInfo(limit, num, userGid));
        return response;
    }

    @Autowired
    private XUserRiskDao xUserRiskDao;

    @GetMapping("riskInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    @ApiOperation("设备信息和位置信息")
    public Response<XUserRisk> riskInfo(@RequestParam String userGid) {
        Response<XUserRisk> response = new Response<>();
        response.setBo(xUserRiskDao.getByUserGid(userGid));
        return response;
    }
}
