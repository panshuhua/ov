package com.ivay.ivay_app.service.impl;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.tempuri.ApiBulkReturn;
import org.tempuri.VMGAPISoapProxy;

import com.ivay.ivay_app.dto.SMSResponseStatus;
import com.ivay.ivay_app.dto.Token;
import com.ivay.ivay_app.dto.XLoginUser;
import com.ivay.ivay_app.service.XConfigService;
import com.ivay.ivay_app.service.XRegisterService;
import com.ivay.ivay_app.service.XTokenService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_common.utils.UUIDUtils;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dto.VerifyCodeInfo;
import com.ivay.ivay_repository.dto.XUser;
import com.ivay.ivay_repository.model.LoginInfo;
import com.ivay.ivay_repository.model.ReturnUser;

@Service
public class XRegisterServiceImpl implements XRegisterService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private XUserInfoDao xUserInfoDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private XTokenService tokenService;
    @Autowired
    @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private XConfigService xConfigService;
    @Autowired
    private I18nService i18nService;
    @Autowired
    private XUserInfoService xUserInfoService;
    @Autowired
    private XTokenService xTokenService;

    // token过期秒数
    @Value("${token.expire.seconds}")
    private Integer expireSeconds;

    @Value("${api_paasoo_url}")
    private String paasooUrl;
    @Value("${api_paasoo_key}")
    private String paasooKey;
    @Value("${api_paasoo_secret}")
    private String paasooSecret;

    @Value("${api_vmgmedia_url}")
    private String vmgmediaUrl;
    @Value("${api_alias}")
    private String alias;
    @Value("${api_authenticate_user}")
    private String authenticateUser;
    @Value("${api_authenticate_pass}")
    private String authenticatePass;
    @Value("${verifycode.effectiveTime}")
    private long effectiveTime;

    @Override
    public XUser addUser(LoginInfo loginInfo) {
        XUser xUserInfo = new XUser();
        xUserInfo.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
        xUserInfo.setUserStatus(SysVariable.USER_STATUS_INIT);
        xUserInfo.setAccountStatus(SysVariable.ACCOUNT_STATUS_NORMAL);
        xUserInfo.setUserGid(UUIDUtils.getUUID());
        xUserInfo.setCreateTime(new Date());
        xUserInfo.setUpdateTime(new Date());
        xUserInfo.setPhone(loginInfo.getMobile());
        xUserInfo.setMacCode(loginInfo.getMacCode());
        xUserInfo.setFmcToken(loginInfo.getFmcToken());

        String password = loginInfo.getPassword();
        if (!StringUtils.isEmpty(password)) {
            xUserInfo.setPassword(bCryptPasswordEncoder.encode(password));
        }
        logger.info("注册insert数据库开始........................");
        return xUserInfoDao.addUser(xUserInfo) == 1 ? xUserInfo : null;
    }

    @Override
    public String getUserGid(String mobile) {
        return xUserInfoDao.getUserGid(mobile);
    }

    @Override
    public boolean checkCode(String key, String value) {
        Object authCode = redisTemplate.opsForValue().get(key);
        logger.info("redis中的key：" + key + "，redis中的value：" + authCode.toString());
        if (authCode != null && authCode.toString().equals(value)) {
            return true;
        }
        return false;
    }

    @Override
    public XUser login(XUser xUser) {
        String mobile = xUser.getPhone();
        String password = xUser.getPassword();
        String fmcToken = xUser.getFmcToken();
        if (!StringUtils.isEmpty(password)) {
            String dbPwd = xUserInfoDao.getPassword(mobile);

            // 前端与数据库密码校验
            boolean flag = bCryptPasswordEncoder.matches(password, dbPwd);

            if (flag) {
                xUser = xUserInfoDao.getLoginUser(mobile, dbPwd);
                // 更新fmcToken
                xUserInfoDao.updateTmcToken(fmcToken, xUser.getUserGid());
                return xUser;
            } else {
                return null;
            }

        } else {
            // 更新fmcToken
            xUserInfoDao.updateTmcToken(xUser.getFmcToken(), xUser.getUserGid());
            return xUser;
        }

    }

    @Override
    public XUser getToken(XUser xUser) {
        XLoginUser xLoginUser = new XLoginUser();
        xLoginUser.setId(xUser.getId());
        xLoginUser.setUserGid(xUser.getUserGid());
        xLoginUser.setPhone(xUser.getPhone());
        Token token = tokenService.saveToken(xLoginUser);
        String userToken = token.getToken();
        xUser.setUserToken(userToken);
        redisTemplate.opsForValue().set(xUser.getUserGid(), userToken, (expireSeconds + 200) * 1000,
            TimeUnit.MILLISECONDS);
        return xUser;
    }

    @Override
    public XUser registerLogin(LoginInfo loginInfo) {
        String password = loginInfo.getPassword();
        logger.info("注册开始........................");
        // 注册
        XUser xUser = addUser(loginInfo);
        logger.info("注册insert数据库结束........................");
        // 更新注册状态
        xUserInfoDao.updateUseStatus(xUser.getUserGid());
        xUser.setUserStatus(SysVariable.USER_STATUS_REGISTRY);
        // 登录
        xUser.setPassword(password);
        xUser = login(xUser);

        // 登录成功生成token
        xUser = getToken(xUser);
        return xUser;
    }

    @Override
    public int updatePassword(String userGid, String mobile, String password) {
        return xUserInfoDao.updatePassword(mobile, password);
    }

    @Override
    public VerifyCodeInfo sendPhoneMsg(String mobile) {

        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_SEND_PHONEMSG));
        if (config == null) {
            logger.error("发送短信验证码配置获取出错");
            return null;
        }

        String authCode = MsgAuthCode.getAuthCode();
        VerifyCodeInfo verifyCodeInfo = new VerifyCodeInfo();
        // long effectiveTime = 120 * 1000; //2分钟有效期，ms
        verifyCodeInfo.setCodeToken(authCode);
        verifyCodeInfo.setEffectiveTime(effectiveTime);

        for (Object key : config.keySet()) {
            String value = config.get(key).toString();

            // 使用方法一发送短信验证码
            if ("1".equals(value)) {
                Map<String, String> msgMap = sendMsgBySMS(mobile, authCode);
                String status = msgMap.get("status");
                logger.info("SMG方式发送短信验证码返回状态，返回码：{}", status);
                verifyCodeInfo.setStatus(status);
                if (SMSResponseStatus.SUCCESS.getCode().equals(status)) {
                    String messageid = msgMap.get("messageid");
                    logger.info("发送的短信id：" + messageid);
                    logger.info("SMG成功发送的短信验证码是：" + authCode);
                    redisTemplate.opsForValue().set(mobile, authCode, effectiveTime, TimeUnit.MILLISECONDS);
                    return verifyCodeInfo;
                }

                // 使用方法二发送短信验证码
            } else if ("2".equals(value)) {
                ApiBulkReturn re = sendMsgByVMG(mobile, authCode);
                String errorCode = Long.toString(re.getError_code());
                verifyCodeInfo.setStatus(errorCode);

                if (SMSResponseStatus.SUCCESS.getCode().equals(errorCode)) {
                    logger.info("VMG发送的短信验证码是：" + authCode);
                    redisTemplate.opsForValue().set(mobile, authCode, effectiveTime, TimeUnit.MILLISECONDS);
                    return verifyCodeInfo;
                }

                // 不发送短信验证码，直接返回随机数（把msg1和msg2都修改为0即可）
            } else if ("0".equals(value)) {
                verifyCodeInfo.setStatus("0");
                logger.info("发送的短信验证码是：" + authCode);
                redisTemplate.opsForValue().set(mobile, authCode, effectiveTime, TimeUnit.MILLISECONDS);
                return verifyCodeInfo;
            }
        }

        return null;

    }

    // 调用接口1发送短信
    @Override
    public Map<String, String> sendMsgBySMS(String mobile, String authCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", paasooKey);
        params.put("secret", paasooSecret);
        params.put("from", "SMS");
        params.put("to", "84" + mobile);
        params.put("text", authCode);
        String ret = restTemplate.getForObject(paasooUrl, String.class, params);
        logger.info("调用短信验证码接口返回ret：" + ret + "------------");
        Map<String, String> msgMap = JsonUtils.jsonToMap(ret);
        return msgMap;
    }

    // 调用接口2发送短信
    @Override
    public ApiBulkReturn sendMsgByVMG(String mobile, String authCode) {
        VMGAPISoapProxy proxy = new VMGAPISoapProxy(vmgmediaUrl);
        String message = authCode;
        String sendTime = "";

        try {
            ApiBulkReturn ret = proxy.bulkSendSms(mobile, alias, message, sendTime, authenticateUser, authenticatePass);
            logger.info("返回状态码：" + ret.getError_code());
            logger.info("返回详细信息：" + ret.getError_detail());
            return ret;
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public VerifyCodeInfo sendRegisterCode(int optType, String mobile) {
        // 注册发送验证码
        if (optType == 1) {
            String userGid = getUserGid(mobile);
            // 如果用户已注册，则提示已注册
            if (!StringUtils.isEmpty(userGid)) {
                logger.info("该手机号已被注册---------------------------");
                throw new BusinessException(i18nService.getMessage("response.error.register.isregister.code"),
                    i18nService.getMessage("response.error.register.isregister.msg"));
            }
        }
        // 重置/修改交易密码
        if (optType == 4) {
            String userGid = getUserGid(mobile);
            boolean flag = xUserInfoService.hasTransPwd(userGid);
            if (!flag) {
                logger.info("交易密码不能为空---------------------------");
                throw new BusinessException(i18nService.getMessage("response.error.borrow.tranpwdempty.code"),
                    i18nService.getMessage("response.error.borrow.tranpwdempty.msg"));
            }
        }

        String existCode = (String)redisTemplate.opsForValue().get(mobile);
        logger.info("该手机号码已存在的验证码：" + existCode + "------------");
        if (!StringUtils.isEmpty(existCode)) {
            logger.info("2分钟内不能使用同一个手机号重复发送手机短信验证码..............");
            throw new BusinessException(i18nService.getMessage("response.error.register.verifycoderepeat.code"),
                i18nService.getMessage("response.error.register.verifycoderepeat.msg"));
        }
        // 调用短信验证码接口
        VerifyCodeInfo verifyCodeInfo = sendPhoneMsg(mobile);
        String status = verifyCodeInfo.getStatus();
        if ("0".equals(status)) {
            return verifyCodeInfo;
        } else {
            // 请求失败
            logger.info("调用第3方接口发送短信失败---------------------------");
            throw new BusinessException(i18nService.getMessage("response.error.register.callfailed.code"),
                i18nService.getMessage("response.error.register.callfailed.msg"));
        }

    }

    @Override
    public ReturnUser register(LoginInfo loginInfo) {
        String mobile = loginInfo.getMobile();
        String macCode = loginInfo.getMacCode();
        logger.info("前台传过来的macCode:" + macCode + "---------------");

        // 手机验证码校验
        String verifyCode = loginInfo.getVerifyCode();
        logger.info("前台传入的手机验证码：" + verifyCode);
        String password = loginInfo.getPassword();
        String isVerifyCodeLogin = loginInfo.getIsVerifyCodeLogin();

        if (!StringUtils.isEmpty(verifyCode)) {
            long existTime = redisTemplate.boundHashOps(mobile).getExpire();
            logger.info("获取到key的有效时间existTime=" + existTime + "--------");

            if (existTime < 0) {
                logger.info("短信验证码已失效============================");
                throw new BusinessException(i18nService.getMessage("response.error.register.verify.code"),
                    i18nService.getMessage("response.error.register.verify.msg"));
            }

            boolean isCorrect = checkCode(mobile, verifyCode);
            logger.info("手机验证码校验结果：" + isCorrect + "--------------");

            if (isCorrect) {
                logger.info("手机验证码校验正确：" + isCorrect + "--------------");
                String userGid = getUserGid(loginInfo.getMobile());

                if (!StringUtils.isEmpty(userGid)) {
                    if (!"1".equals(isVerifyCodeLogin)) {
                        logger.info("该用户已注册---------------------------");
                        throw new BusinessException(i18nService.getMessage("response.error.register.isregister.code"),
                            i18nService.getMessage("response.error.register.isregister.msg"));

                    } else {
                        // 短信验证码登录：已经注册过，直接登录
                        XUser xUser = new XUser();
                        xUser.setPhone(mobile);
                        xUser.setUserGid(userGid);
                        xUser.setCreateTime(new Date());
                        xUser.setFmcToken(loginInfo.getFmcToken());
                        xUser = login(xUser);
                        xUser = getToken(xUser);
                        ReturnUser user = setReturnUser(xUser);
                        user.setNeedverifyMapCode(0);
                        // 标识告诉前台是登录还是注册
                        user.setType(SysVariable.RETURN_TYPE_LOGIN);
                        return user;
                    }

                } else {
                    // 密码注册-自动登录：这里才需要校验密码
                    if (!StringUtils.isEmpty(password)) {
                        if (!StringUtil.valiPassword(password)) {
                            logger.info("用户名和密码输入错误--------------------");
                            throw new BusinessException(
                                i18nService.getMessage("response.error.register.passworderror.code"),
                                i18nService.getMessage("response.error.register.passworderror.msg"));
                        }
                        logger.info("密码注册：" + macCode + "-------------------");
                        String macCodeRepeatPhone = xUserInfoService.checkMacCode(macCode);
                        if (StringUtils.isEmpty(macCodeRepeatPhone)) {
                            XUser xUser = registerLogin(loginInfo);
                            ReturnUser user = setReturnUser(xUser);
                            user.setNeedverifyMapCode(0);
                            return user;
                        } else {
                            // 使用了同一个设备注册，返回错误信息
                            String msg = MessageFormat.format(
                                i18nService.getMessage("response.error.register.macCodeRepeat.msg"),
                                macCodeRepeatPhone);
                            throw new BusinessException(
                                i18nService.getMessage("response.error.register.macCodeRepeat.code"), msg);
                        }

                    } else {
                        if (!"1".equals(isVerifyCodeLogin)) {
                            throw new BusinessException(
                                i18nService.getMessage("response.error.register.blankpassword.code"),
                                i18nService.getMessage("response.error.register.blankpassword.msg"));
                        } else {
                            // 短信验证码登录：还未注册，短信验证码注册后再自动登录
                            logger.info("短信验证码注册：" + macCode + "-------------------");
                            String macCodeRepeatPhone = xUserInfoService.checkMacCode(macCode);
                            if (StringUtils.isEmpty(macCodeRepeatPhone)) {
                                XUser xUser = registerLogin(loginInfo);
                                ReturnUser user = setReturnUser(xUser);
                                user.setNeedverifyMapCode(0);
                                // 标识告诉前台是登录还是注册
                                user.setType(SysVariable.RETURN_TYPE_REGISTER);
                                return user;
                            } else {
                                // 使用了同一个设备注册，返回错误信息
                                String msg = MessageFormat.format(
                                    i18nService.getMessage("response.error.register.macCodeRepeat.msg"),
                                    macCodeRepeatPhone);
                                throw new BusinessException(
                                    i18nService.getMessage("response.error.register.macCodeRepeat.code"), msg);
                            }

                        }

                    }
                }

            } else {
                logger.info("手机验证码校验错误：" + isCorrect + "--------------");
                // 验证码错误次数
                throw new BusinessException(i18nService.getMessage("response.error.register.verifynum.code"),
                    i18nService.getMessage("response.error.register.verifynum.msg"));

            }

        } else {
            throw new BusinessException(i18nService.getMessage("response.error.register.phoneverify.code"),
                i18nService.getMessage("response.error.register.phoneverify.msg"));

        }

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

    @Override
    public ReturnUser login(LoginInfo loginInfo) {
        String mobile = loginInfo.getMobile();
        String password = loginInfo.getPassword();
        String needCheckVerify = loginInfo.getNeedCheckVerify();
        logger.info("前台传过来的fmcToken:" + loginInfo.getFmcToken() + "============================");
        boolean isCorrect = true;
        // 老用户新设备登录，需要校验手机验证码
        String verifyCode = loginInfo.getVerifyCode();
        if ("1".equals(needCheckVerify)) {
            if (!StringUtils.isEmpty(verifyCode)) {
                long existTime = redisTemplate.boundHashOps(mobile).getExpire();
                logger.info("获取到key的有效时间existTime=" + existTime + "--------");

                if (existTime < 0) {
                    logger.info("手机验证码失效============================");
                    throw new BusinessException(i18nService.getMessage("response.error.register.verify.code"),
                        i18nService.getMessage("response.error.register.verify.msg"));
                }
                isCorrect = checkCode(mobile, verifyCode);
            } else {
                isCorrect = false;
                throw new BusinessException(i18nService.getMessage("response.error.register.phoneverify.code"),
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
                xUser = login(xUser);
                // 登录成功生成token
                if (xUser != null) {
                    xUser = getToken(xUser);
                    ReturnUser user = setReturnUser(xUser);
                    user.setNeedverifyMapCode(0);
                    return user;
                } else {
                    throw new BusinessException(i18nService.getMessage("response.error.register.loginfail.code"),
                        i18nService.getMessage("response.error.register.loginfail.msg"));
                }

            } else {
                throw new BusinessException(i18nService.getMessage("response.error.register.verifynum.code"),
                    i18nService.getMessage("response.error.register.verifynum.msg"));
            }

        } else {
            throw new BusinessException(i18nService.getMessage("response.error.register.password.code"),
                i18nService.getMessage("response.error.register.password.msg"));
        }

    }

    @Override
    public void logout(String userGid) {
        String token = (String)redisTemplate.opsForValue().get(userGid);
        if (!StringUtils.isEmpty(token)) {
            redisTemplate.delete(userGid);
            xTokenService.deleteToken(token);
        } else {
            throw new BusinessException(i18nService.getMessage("response.error.register.logoutfail.code"),
                i18nService.getMessage("response.error.register.logoutfail.msg"));
        }

    }

    @Override
    public void resetPwd(String mobile, String verifyCode, String password) {
        // 判断手机号和验证码是否匹配
        boolean isCorrect = true;
        if (!StringUtils.isEmpty(verifyCode)) {
            long existTime = redisTemplate.boundHashOps(mobile).getExpire();
            logger.info("获取到key的有效时间existTime=" + existTime + "--------");
            if (existTime < 0) {
                logger.info("短信验证码已失效============================");
                throw new BusinessException(i18nService.getMessage("response.error.register.verify.code"),
                    i18nService.getMessage("response.error.register.verify.msg"));
            }
            isCorrect = checkCode(mobile, verifyCode);
        }

        // 修改密码
        if (isCorrect) {
            password = bCryptPasswordEncoder.encode(password);
            String userGid = getUserGid(mobile);
            if (!StringUtils.isEmpty(userGid)) {
                updatePassword(userGid, mobile, password);
            } else {
                throw new BusinessException(i18nService.getMessage("response.error.register.phoneerror.code"),
                    i18nService.getMessage("response.error.register.phoneerror.msg"));
            }

        } else {
            throw new BusinessException(i18nService.getMessage("response.error.register.verifynum.code"),
                i18nService.getMessage("response.error.register.verifynum.msg"));
        }

    }

}
