package com.ivay.ivay_manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_manage.service.VerifyCodeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("manage/verifyCode")
@Api(tags = "短信验证码")
public class VerifyCodeController {

    @Autowired
    VerifyCodeService verifyCodeService;

    @GetMapping("getVerifyCode")
    @ApiOperation("获取短信验证码")
    @ApiImplicitParam(name = "phone", value = "手机号码", dataType = "String", paramType = "query")
    public String getVerifyCode(@RequestParam String phone) {
        // 获取短信验证码
        String verifyCode = verifyCodeService.getVerifyCode(phone);
        return verifyCode;
    }

}
