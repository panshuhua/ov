package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.service.XRegisterService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_common.annotation.Decrypt;
import com.ivay.ivay_common.annotation.Encryption;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_common.valid.IdentityCard;
import com.ivay.ivay_common.valid.Password;
import com.ivay.ivay_common.valid.Update;
import com.ivay.ivay_repository.dto.CreditLine;
import com.ivay.ivay_repository.dto.VerifyCodeInfo;
import com.ivay.ivay_repository.model.XUserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("star/userInfos")
@Api(tags = "授信")
@Validated
public class XUserInfoController {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private XUserInfoService xUserInfoService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private XRegisterService registerService;

    @Autowired
    private I18nService i18nService;

    @PostMapping("update")
    @ApiOperation(value = "编辑")
    @LogAnnotation(module = "编辑授信信息")
    public Response<XUserInfo> update(@Validated({Update.class}) @RequestBody XUserInfo xUserInfo, HttpServletRequest request) {
        Response<XUserInfo> response = new Response<>();
        response.setBo(xUserInfoService.update(xUserInfo));
        return response;
    }

    @GetMapping("get")
    @ApiOperation(value = "获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    @LogAnnotation(module = "获取授信信息")
    public Response<XUserInfo> get(@RequestParam String userGid, HttpServletRequest request) {
        Response<XUserInfo> response = new Response<>();
        response.setBo(xUserInfoService.getByGid(userGid));
        return response;
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    @LogAnnotation(module = "删除授信信息")
    public void delete(@RequestParam String gid, HttpServletRequest request) {
        xUserInfoService.delete(gid);
    }

    @GetMapping("getcreditLine/{gid}")
    @ApiOperation(value = "获取授信额度")
    @LogAnnotation(module = "获取授信额度")
    public Response<CreditLine> getCreditLine(@PathVariable String gid, HttpServletRequest request) {
        Response<CreditLine> response = new Response<>();
        //验证userGid有效性
        XUserInfo xUserInfo = xUserInfoService.getByGid(gid);
        if (xUserInfo != null) {
            response.setBo(xUserInfoService.getCreditLine(gid));
        } else {
            response.setStatus(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
        }
        return response;
    }

    @GetMapping("getUserStatus")
    @ApiOperation(value = "获取授信认证信息状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    @LogAnnotation(module = "获取授信认证信息状态")
    public Response<String> getUserStatus(@RequestParam String gid, HttpServletRequest request) {
        Response<String> response = new Response<>();
        response.setBo(xUserInfoService.getUserStatus(gid));
        return response;
    }

    @GetMapping("hasTransPwd")
    @ApiOperation(value = "是否有交易密码")
    @LogAnnotation(module = "查询是否有交易密码")
    public Response<String> hasTransPwd(@RequestParam String userGid, HttpServletRequest request) {
        Response<String> response = new Response<>();
        response.setBo(xUserInfoService.hasTransPwd(userGid) ? SysVariable.TRANSFER_PWD_HAS : SysVariable.TRANSFER_PWD_NONE);
        return response;
    }

    @PostMapping("setTransPwd")
    @ApiOperation(value = "设置交易密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "交易密码", dataType = "String", paramType = "query")
    })
    @Encryption
    @LogAnnotation(module = "设置交易密码")
    public Response<String> setTransPwd(@Password @RequestParam String password,
                                        @RequestParam String userGid, HttpServletRequest request) {
        Response<String> response = new Response<>();
        // 交易密码不允许修改
        if (xUserInfoService.hasTransPwd(userGid)) {
            response.setStatus(i18nService.getMessage("response.error.borrow.checktranpwd.code"),
                    i18nService.getMessage("response.error.borrow.checktranpwd.msg"));
        } else {
            xUserInfoService.setTransPwd(userGid, password);
            response.setStatus(i18nService.getMessage("response.success.borrow.settranpwd.code"),
                    i18nService.getMessage("response.success.borrow.settranpwd.msg"));
        }
        return response;
    }

    @GetMapping("checkIdentity")
    @ApiOperation("修改交易密码前校验身份")
    @Encryption
    @LogAnnotation(module = "修改交易密码前校验身份")
    public Response<VerifyCodeInfo> checkIdentity(@RequestParam @Decrypt String mobile,
                                                  @RequestParam String verifyCode,
                                                  @RequestParam String userGid,
                                                  @RequestParam @IdentityCard String identityCard, HttpServletRequest request) {
        Response<VerifyCodeInfo> response = new Response<>();
        if (!StringUtils.isEmpty(verifyCode)) {
            long existTime = redisTemplate.boundHashOps(mobile).getExpire();
            logger.info("获取到key的有效时间existTime=" + existTime + "--------");
            if (existTime < 0) {
                response.setStatus(i18nService.getMessage("response.error.register.verify.code"),
                        i18nService.getMessage("response.error.register.verify.msg"));
                return response;
            }


            //判断手机号和验证码是否匹配
            if (registerService.checkCode(mobile, verifyCode)) {
                VerifyCodeInfo code = xUserInfoService.checkIdentify(userGid, identityCard);
                if (code != null) {
                    response.setBo(code);
                    return response;
                }

                response.setStatus(i18nService.getMessage("response.error.borrow.checkIdentify.code"),
                        i18nService.getMessage("response.error.borrow.checkIdentify.msg"));

            } else {
                response.setStatus(i18nService.getMessage("response.error.register.verifynum.code"),
                        i18nService.getMessage("response.error.register.verifynum.msg"));
            }
        }

        return response;
    }

    @PostMapping("updateTransPwd")
    @ApiOperation(value = "修改交易密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "交易密码", dataType = "String", paramType = "query")
    })
    @Encryption
    @LogAnnotation(module = "修改交易密码")
    public Response<String> updateTransPwd(@RequestParam String codeToken,
                                           @RequestParam String userGid,
                                           @Password @RequestParam String password, HttpServletRequest request) {
        Response<String> response = new Response<>();
        long existTime = redisTemplate.boundHashOps(userGid).getExpire();
        logger.info("获取到key的有效时间existTime=" + existTime + "--------");
        if (existTime < 0) {
            response.setStatus(i18nService.getMessage("response.error.borrow.tokentimeout.code"),
                    i18nService.getMessage("response.error.borrow.tokentimeout.msg"));
            return response;
        }

        String token = redisTemplate.opsForValue().get(userGid);
        if (!StringUtils.isEmpty(token)) {
            token = token.replace("\"", "");
        }

        System.out.println(token);
        System.out.println(codeToken);
        System.out.println(codeToken.equals(token));
        //codeToken校验成功才能重置交易密码
        if (!codeToken.equals(token)) {
            response.setStatus(i18nService.getMessage("response.error.borrow.tokenerror.code"),
                    i18nService.getMessage("response.error.borrow.tokenerror.msg"));
            return response;
        }

        xUserInfoService.setTransPwd(userGid, password);

        return response;
    }
}
