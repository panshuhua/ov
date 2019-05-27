package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.dao.XUserInfoDao;
import com.ivay.ivay_app.dto.SMSResponseStatus;
import com.ivay.ivay_app.dto.Token;
import com.ivay.ivay_app.dto.XLoginUser;
import com.ivay.ivay_app.model.LoginInfo;
import com.ivay.ivay_app.model.VerifyCodeInfo;
import com.ivay.ivay_app.model.XUser;
import com.ivay.ivay_app.service.RegisterService;
import com.ivay.ivay_app.service.XConfigService;
import com.ivay.ivay_app.service.XTokenService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_app.utils.JsonUtils;
import com.ivay.ivay_app.utils.MsgAuthCode;
import com.ivay.ivay_app.utils.SysVariable;
import com.ivay.ivay_app.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.tempuri.ApiBulkReturn;
import org.tempuri.VMGAPISoapProxy;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RegisterServiceImpl implements RegisterService {
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private XConfigService xConfigService;
    @Autowired
    private I18nService i18nService;
    @Autowired
    private XUserInfoService xUserInfoService;

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
        xUserInfo.setLongitude(loginInfo.getLongitude());
        xUserInfo.setLatitude(loginInfo.getLatitude());

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
        if (!StringUtils.isEmpty(password)) {
            String dbPwd = xUserInfoDao.getPassword(mobile);

            //前端与数据库密码校验
            boolean flag = bCryptPasswordEncoder.matches(password, dbPwd);

            if (flag) {
                xUser = xUserInfoDao.getLoginUser(mobile, dbPwd);
                return xUser;
            } else {
                return null;
            }

        } else {
            return xUser;
        }


    }

    @Override
    public XUser getToken(XUser xUser) {
        XLoginUser xLoginUser = new XLoginUser();
        xLoginUser.setId(xUser.getId());
        Token token = tokenService.saveToken(xLoginUser);
        String userToken = token.getToken();
        xUser.setUserToken(userToken);
        redisTemplate.opsForValue().set(xUser.getUserGid(), userToken, xLoginUser.getExpireTime(), TimeUnit.SECONDS);
        return xUser;
    }

    @Override
    public XUser registerLogin(LoginInfo loginInfo) {
        String password = loginInfo.getPassword();
        logger.info("注册开始........................");
        //注册
        XUser xUser = addUser(loginInfo);
        logger.info("注册insert数据库结束........................");
        //更新注册状态
        xUserInfoDao.updateUseStatus(xUser.getUserGid());
        xUser.setUserStatus(SysVariable.USER_STATUS_REGISTRY);
        //登录
        xUser.setPassword(password);
        xUser = login(xUser);

        //登录成功生成token
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
            logger.error("借款利率配置获取出错");
            return null;
        }

        String authCode = MsgAuthCode.getAuthCode();
        VerifyCodeInfo verifyCodeInfo = new VerifyCodeInfo();
        long effectiveTime = 120 * 1000; //2分钟有效期，ms
        verifyCodeInfo.setCodeToken(authCode);
        verifyCodeInfo.setEffectiveTime(effectiveTime);

        for (Object key : config.keySet()) {
            String value = config.get(key).toString();

            //使用方法一发送短信验证码
            if ("1".equals(value)) {
                Map<String, String> msgMap = sendMsgBySMS(mobile, authCode);
                String status = msgMap.get("status");
                logger.info("SMS方式发送短信验证码返回状态，返回码：{}，说明：{}", status, msgMap.get("status_code"));
                verifyCodeInfo.setStatus(status);
                if (SMSResponseStatus.SUCCESS.getCode().equals(status)) {
                    String messageid = msgMap.get("messageid");
                    logger.info("发送的短信id：" + messageid);
                    logger.info("SMG成功发送的短信验证码是：" + authCode);
                    redisTemplate.opsForValue().set(mobile, authCode, effectiveTime, TimeUnit.MILLISECONDS);
                    return verifyCodeInfo;
                }

                //使用方法二发送短信验证码
            } else if ("2".equals(value)) {
                ApiBulkReturn re = sendMsgByVMG(mobile, authCode);
                String errorCode = Long.toString(re.getError_code());
                verifyCodeInfo.setStatus(errorCode);

                if ("0".equals(errorCode)) {
                    logger.info("VMG发送的短信验证码是：" + authCode);
                    redisTemplate.opsForValue().set(mobile, authCode, effectiveTime, TimeUnit.MILLISECONDS);
                    return verifyCodeInfo;
                }

                //不发送短信验证码，直接返回随机数（把msg1和msg2都修改为0即可）
            } else if ("0".equals(value)) {
                verifyCodeInfo.setStatus("0");
                redisTemplate.opsForValue().set(mobile, authCode, effectiveTime, TimeUnit.MILLISECONDS);
                return verifyCodeInfo;
            }
        }

        return null;

    }

    //调用接口1发送短信
    private Map<String, String> sendMsgBySMS(String mobile, String authCode) {
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

    //调用接口2发送短信
    private ApiBulkReturn sendMsgByVMG(String mobile, String authCode) {
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

}
