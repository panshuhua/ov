package com.ivay.ivay_manage.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_manage.service.VerifyCodeService;

@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${verifycode.validTime}") // 短信验证码失效时间：10分钟
    private long validTime;

    @Override
    public String getVerifyCode(String phone) {
        String authCode = MsgAuthCode.getAuthCode();
        redisTemplate.opsForValue().set(phone, authCode, validTime, TimeUnit.MILLISECONDS); // 短信验证码有效时间
        return authCode;
    }

}
