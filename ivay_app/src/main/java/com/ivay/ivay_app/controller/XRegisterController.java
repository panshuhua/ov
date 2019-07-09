package com.ivay.ivay_app.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_app.service.XRegisterService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_common.annotation.Decrypt;
import com.ivay.ivay_common.annotation.Encryption;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.valid.Password;
import com.ivay.ivay_common.valid.Update;
import com.ivay.ivay_repository.dto.VerifyCodeInfo;
import com.ivay.ivay_repository.model.LoginInfo;
import com.ivay.ivay_repository.model.ReturnUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("star/register")
@Api(tags = "注册与登录")
@Validated
public class XRegisterController {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private XRegisterService xRegisterService;

    @Value("${api_paasoo_url}")
    private String paasooUrl;
    @Value("${api_paasoo_key}")
    private String paasooKey;
    @Value("${api_paasoo_secret}")
    private String paasooSecret;

    @PostMapping("sendVerifyCode")
    @ApiOperation(value = "发送手机验证码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "mobile", value = "用户手机号码", dataType = "String", paramType = "query", required = true),
        @ApiImplicitParam(name = "optType", value = "操作类型", dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(name = "macCode", value = "设备id", dataType = "String", paramType = "query")})
    @Encryption
    @LogAnnotation(module = "发送短信验证码")
    public Response<VerifyCodeInfo> sendRegisterCode(@RequestParam @Decrypt String mobile,
        @RequestParam Integer optType, @RequestParam(required = false) String macCode, HttpServletRequest request) {
        logger.info("进入发送短信验证码的方法，前台传入的手机号码为:{}", mobile);
        Response<VerifyCodeInfo> response = new Response<>();
        response.setBo(xRegisterService.sendRegisterCode(optType, mobile));
        return response;
    }

    @PostMapping("reg")
    @ApiOperation(value = "用户注册")
    @LogAnnotation(module = "用户注册与短信验证码登录")
    public Response<ReturnUser> register(@RequestBody LoginInfo loginInfo, HttpServletRequest request) {
        Response<ReturnUser> response = new Response<>();
        response.setBo(xRegisterService.register(loginInfo));
        return response;
    }

    @PostMapping("login")
    @ApiOperation(value = "用户登录")
    @LogAnnotation(module = "用户登录(用户名和密码登录)")
    public Response<ReturnUser> login(@Validated({Update.class}) @RequestBody LoginInfo loginInfo) {
        Response<ReturnUser> response = new Response<>();
        response.setBo(xRegisterService.login(loginInfo));
        return response;
    }

    @GetMapping("/logout/{userGid}")
    @ApiOperation(value = "用户注销")
    @LogAnnotation(module = "用户退出")
    public Response<String> logout(@PathVariable String userGid, HttpServletRequest request) {
        Response<String> response = new Response<>();
        xRegisterService.logout(userGid);
        return response;
    }

    @PostMapping("/resetpwd")
    @ApiOperation(value = "重设密码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "mobile", value = "用户手机号码", dataType = "String", paramType = "query", required = true),
        @ApiImplicitParam(name = "verifyCode", value = "手机短信验证码", dataType = "String", paramType = "query",
            required = true),
        @ApiImplicitParam(name = "password", value = "新设的密码", dataType = "String", paramType = "query",
            required = true)})
    @Encryption
    @LogAnnotation(module = "重设登录密码")
    public Response<String> resetPwd(@RequestParam String mobile, @RequestParam String verifyCode,
        @RequestParam @Password(type = "1", message = "validated.loginpassword.error") String password) {
        Response<String> response = new Response<>();
        xRegisterService.resetPwd(mobile, verifyCode, password);
        return response;
    }

    @Autowired
    private XUserInfoService xUserInfoService;

    @GetMapping("checkMacCode")
    @ApiOperation("检测手机注册手机号")
    public Response<String> checkMacCode(@RequestParam String macCode) {
        Response<String> response = new Response<>();
        response.setBo(xUserInfoService.checkMacCode(macCode));
        return response;
    }
}
