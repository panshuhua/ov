package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.dto.Response;
import com.ivay.ivay_app.service.RegisterService;
import com.ivay.ivay_app.service.XTokenService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_common.annotation.Decrypt;
import com.ivay.ivay_common.annotation.Encryption;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.valid.Password;
import com.ivay.ivay_common.valid.Update;
import com.ivay.ivay_repository.model.LoginInfo;
import com.ivay.ivay_repository.model.ReturnUser;
import com.ivay.ivay_repository.model.VerifyCodeInfo;
import com.ivay.ivay_repository.model.XUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("star/register")
@Api(tags = "注册")
@Validated
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private I18nService i18nService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private XUserInfoService xUserInfoService;
    @Autowired
    private XTokenService xTokenService;

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
            @ApiImplicitParam(name = "macCode", value = "设备id", dataType = "String", paramType = "query")
    })
    @Encryption
    public Response<VerifyCodeInfo> sendRegisterCode(@RequestParam @Decrypt String mobile,
                                                     @RequestParam Integer optType,
                                                     @RequestParam(required = false) String macCode
            , HttpServletRequest request) {
        logger.info("前台传过来的请求头：" + request.getHeader("Accept-Language"));

        Response<VerifyCodeInfo> response = new Response<>();
        //注册发送验证码
        if (optType == 1) {
            String userGid = registerService.getUserGid(mobile);
            //如果用户已注册，则提示已注册
            if (!StringUtils.isEmpty(userGid)) {
                response.setStatus(i18nService.getMessage("response.error.register.isregister.code"), i18nService.getMessage("response.error.register.isregister.msg"));
                logger.info(response.getStatus().getMessage() + "：---------------------------");
                return response;
            }
        }
        //重置/修改交易密码
        if (optType == 4) {
            String userGid = registerService.getUserGid(mobile);
            boolean flag = xUserInfoService.hasTransPwd(userGid);
            if (!flag) {
                response.setStatus(i18nService.getMessage("response.error.borrow.tranpwdempty.code"), i18nService.getMessage("response.error.borrow.tranpwdempty.msg"));
                return response;
            }
        }
        logger.info("进入发送短信验证码的方法：" + "手机号：" + mobile + "----------------");
        String existCode = (String) redisTemplate.opsForValue().get(mobile);
        logger.info("该手机号码已存在的验证码：" + existCode + "------------");
        if (!StringUtils.isEmpty(existCode)) {
            logger.info("2分钟内不能使用同一个手机号重复发送手机短信验证码..............");
            response.setStatus(i18nService.getMessage("response.error.register.verifycoderepeat.code"), i18nService.getMessage("response.error.register.verifycoderepeat.msg"));
            return response;
        }
        //调用短信验证码接口
        VerifyCodeInfo verifyCodeInfo = registerService.sendPhoneMsg(mobile);
        String status = verifyCodeInfo.getStatus();
        if ("0".equals(status)) {
            response.setBo(verifyCodeInfo);
        } else {
            //请求失败
            response.setStatus(i18nService.getMessage("response.error.register.callfailed.code"), i18nService.getMessage("response.error.register.callfailed.msg"));
        }

        return response;
    }

    @PostMapping("reg")
    @ApiOperation(value = "用户注册")
    public Response<ReturnUser> register(@RequestBody LoginInfo loginInfo, HttpServletRequest request) {
        logger.info("前台传过来的请求头：" + request.getHeader("Accept-Language"));
        Response<ReturnUser> response = new Response<>();
        String mobile = loginInfo.getMobile();

        //手机验证码校验
        String verifyCode = loginInfo.getVerifyCode();
        logger.info("前台传入的手机验证码：" + verifyCode);
        String password = loginInfo.getPassword();
        String isVerifyCodeLogin = loginInfo.getIsVerifyCodeLogin();

        if (!StringUtils.isEmpty(verifyCode)) {
            long existTime = redisTemplate.boundHashOps(mobile).getExpire();
            logger.info("获取到key的有效时间existTime=" + existTime + "--------");

            if (existTime < 0) {
                response.setStatus(i18nService.getMessage("response.error.register.verify.code"),
                        i18nService.getMessage("response.error.register.verify.msg"));
                logger.info(response.getStatus().getMessage() + "：============================");
                return response;
            }

            boolean isCorrect = registerService.checkCode(mobile, verifyCode);
            logger.info("手机验证码校验结果：" + isCorrect + "--------------");

            if (isCorrect) {
                logger.info("手机验证码校验正确：" + isCorrect + "--------------");
                String userGid = registerService.getUserGid(loginInfo.getMobile());

                if (!StringUtils.isEmpty(userGid)) {
                    if (!"1".equals(isVerifyCodeLogin)) {
                        response.setStatus(i18nService.getMessage("response.error.register.isregister.code"),
                                i18nService.getMessage("response.error.register.isregister.msg"));
                        logger.info(response.getStatus().getMessage() + "：---------------------------");
                    } else {
                        //短信验证码登录：已经注册过，直接登录
                        XUser xUser = new XUser();
                        xUser.setPhone(mobile);
                        xUser.setUserGid(userGid);
                        xUser.setCreateTime(new Date());
                        xUser.setFmcToken(loginInfo.getFmcToken());
                        xUser = registerService.login(xUser);
                        xUser = registerService.getToken(xUser);
                        ReturnUser user = setReturnUser(xUser);
                        user.setNeedverifyMapCode(0);
                        response.setBo(user);
                        response.setStatus(i18nService.getMessage("response.error.register.loginsuccess.code"),
                                i18nService.getMessage("response.error.register.loginsuccess.msg"));
                    }

                    return response;

                } else {
                    //密码注册-自动登录：这里才需要校验密码
                    if (!StringUtils.isEmpty(password)) {
                        if (!StringUtil.valiPassword(password)) {
                            response.setStatus(i18nService.getMessage("response.error.register.passworderror.code"),
                                    i18nService.getMessage("response.error.register.passworderror.msg"));
                            return response;
                        }
                        XUser xUser = registerService.registerLogin(loginInfo);
                        ReturnUser user = setReturnUser(xUser);
                        user.setNeedverifyMapCode(0);
                        response.setBo(user);
                        response.setStatus(i18nService.getMessage("response.success.register.code"),
                                i18nService.getMessage("response.success.register.msg"));
                        return response;
                    } else {
                        if (!"1".equals(isVerifyCodeLogin)) {
                            response.setStatus(i18nService.getMessage("response.error.register.blankpassword.code"),
                                    i18nService.getMessage("response.error.register.blankpassword.msg"));
                            return response;
                        } else {
                            //短信验证码登录：还未注册，短信验证码注册后再自动登录
                            XUser xUser = registerService.registerLogin(loginInfo);
                            ReturnUser user = setReturnUser(xUser);
                            user.setNeedverifyMapCode(0);
                            response.setBo(user);
                            response.setStatus(i18nService.getMessage("response.error.register.loginsuccess.code"),
                                    i18nService.getMessage("response.error.register.loginsuccess.msg"));
                            return response;
                        }


                    }
                }

            } else {
                logger.info("手机验证码校验错误：" + isCorrect + "--------------");
                //验证码错误次数
                response.setStatus(i18nService.getMessage("response.error.register.verifynum.code"),
                        i18nService.getMessage("response.error.register.verifynum.msg"));
                return response;

            }

        } else {
            response.setStatus(i18nService.getMessage("response.error.register.phoneverify.code"),
                    i18nService.getMessage("response.error.register.phoneverify.msg"));

        }


        return response;
    }


    @PostMapping("login")
    @ApiOperation(value = "用户登录")
    public Response<ReturnUser> login(@Validated({Update.class}) @RequestBody LoginInfo loginInfo) {
        Response<ReturnUser> response = new Response<>();
        String mobile = loginInfo.getMobile();
        String password = loginInfo.getPassword();
        String needCheckVerify = loginInfo.getNeedCheckVerify();
        logger.info("前台传过来的fmcToken:" + loginInfo.getFmcToken() + "============================");
        boolean isCorrect = true;
        //老用户新设备登录，需要校验手机验证码
        String verifyCode = loginInfo.getVerifyCode();
        if ("1".equals(needCheckVerify)) {
            if (!StringUtils.isEmpty(verifyCode)) {
                long existTime = redisTemplate.boundHashOps(mobile).getExpire();
                logger.info("获取到key的有效时间existTime=" + existTime + "--------");

                if (existTime < 0) {
                    response.setStatus(i18nService.getMessage("response.error.register.verify.code"),
                            i18nService.getMessage("response.error.register.verify.msg"));
                    logger.info(response.getStatus().getMessage() + "：============================");
                    return response;
                }
                isCorrect = registerService.checkCode(mobile, verifyCode);
            } else {
                isCorrect = false;
                response.setStatus(i18nService.getMessage("response.error.register.phoneverify.code"),
                        i18nService.getMessage("response.error.register.phoneverify.msg"));
            }

        }

        if (!StringUtils.isEmpty(password)) {
            XUser xUser = new XUser();
            xUser.setPhone(mobile);
            xUser.setPassword(password);
            xUser.setFmcToken(loginInfo.getFmcToken());
            if (isCorrect) {
                xUser.setNeedverifyMapCode(0);
                xUser = registerService.login(xUser);
                //登录成功生成token
                if (xUser != null) {
                    xUser = registerService.getToken(xUser);
                    ReturnUser user = setReturnUser(xUser);
                    user.setNeedverifyMapCode(0);
                    response.setBo(user);
                    response.setStatus(i18nService.getMessage("response.error.register.loginsuccess.code"),
                            i18nService.getMessage("response.error.register.loginsuccess.msg"));

                } else {
                    response.setStatus(i18nService.getMessage("response.error.register.loginfail.code"),
                            i18nService.getMessage("response.error.register.loginfail.msg"));
                }

            } else {
                response.setStatus(i18nService.getMessage("response.error.register.verifynum.code"),
                        i18nService.getMessage("response.error.register.verifynum.msg"));

            }

        } else {
            response.setStatus(i18nService.getMessage("response.error.register.password.code"),
                    i18nService.getMessage("response.error.register.password.msg"));
        }

        return response;
    }

    @GetMapping("/logout/{userGid}")
    @ApiOperation(value = "用户注销")
    public Response<String> logout(@PathVariable String userGid) {
        Response<String> response = new Response<>();
        String token = (String) redisTemplate.opsForValue().get(userGid);
        if (!StringUtils.isEmpty(token)) {
            redisTemplate.delete(userGid);
            xTokenService.deleteToken(token);
            response.setStatus(i18nService.getMessage("response.success.register.logout.code"),
                    i18nService.getMessage("response.success.register.logout.msg"));
        } else {
            response.setStatus(i18nService.getMessage("response.error.register.logoutfail.code"),
                    i18nService.getMessage("response.error.register.logoutfail.msg"));
        }

        return response;
    }

    @PostMapping("/resetpwd")
    @ApiOperation(value = "重设密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "用户手机号码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "verifyCode", value = "手机短信验证码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "新设的密码", dataType = "String", paramType = "query", required = true)
    })
    @Encryption
    public Response<String> resetPwd(@RequestParam String mobile,
                                     @RequestParam String verifyCode,
                                     @RequestParam @Password(type = "1", message = "validated.loginpassword.error") String password) {
        Response<String> response = new Response<>();
        //判断手机号和验证码是否匹配
        boolean isCorrect = true;
        if (!StringUtils.isEmpty(verifyCode)) {
            long existTime = redisTemplate.boundHashOps(mobile).getExpire();
            logger.info("获取到key的有效时间existTime=" + existTime + "--------");
            if (existTime < 0) {
                response.setStatus(i18nService.getMessage("response.error.register.verify.code"),
                        i18nService.getMessage("response.error.register.verify.msg"));
                logger.info(response.getStatus().getMessage() + "：============================");
                return response;
            }
            isCorrect = registerService.checkCode(mobile, verifyCode);
        }

        //修改密码
        if (isCorrect) {
            password = bCryptPasswordEncoder.encode(password);
            String userGid = registerService.getUserGid(mobile);
            if (!StringUtils.isEmpty(userGid)) {
                registerService.updatePassword(userGid, mobile, password);
            } else {
                response.setStatus(i18nService.getMessage("response.error.register.phoneerror.code"),
                        i18nService.getMessage("response.error.register.phoneerror.msg"));
            }

        } else {
            response.setStatus(i18nService.getMessage("response.error.register.verifynum.code"),
                    i18nService.getMessage("response.error.register.verifynum.msg"));
        }

        return response;
    }

    private ReturnUser setReturnUser(XUser xUser) {
        ReturnUser user = new ReturnUser();
        user.setUserGid(xUser.getUserGid());
        user.setUserStatus(xUser.getUserStatus());
        user.setUserToken(xUser.getUserToken());
        user.setAccountStatus(xUser.getAccountStatus());
        user.setName(xUser.getName());
        user.setSex(xUser.getSex());
        user.setMobile(xUser.getPhone());

        if (StringUtils.isEmpty(xUser.getCanborrowAmount())) {
            user.setCanborrowAmount(0);
        } else {
            user.setCanborrowAmount(xUser.getCanborrowAmount());
        }

        if (StringUtils.isEmpty(xUser.getCreditLine())) {
            user.setCreditLine(0);
        } else {
            user.setCreditLine(xUser.getCreditLine());
        }

        return user;
    }


}
