package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dto.Token;
import com.ivay.ivay_app.dto.XLoginUser;
import com.ivay.ivay_app.service.SysLogService;
import com.ivay.ivay_app.service.XTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token存到redis的实现类<br>
 * jwt实现的token
 *
 * @author xx
 */
@Primary
@Service
public class XTokenServiceJWTImpl implements XTokenService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    /**
     * token过期秒数
     */
    @Value("${token.expire.seconds}")
    private Integer expireSeconds;
    @Autowired
    private RedisTemplate<String, XLoginUser> redisTemplate;
    @Autowired
    private SysLogService logService;
    /**
     * 私钥
     */
    @Value("${token.jwtSecret}")
    private String jwtSecret;

    private static Key KEY = null;
    private static final String LOGIN_USER_KEY = "LOGIN_USER_KEY";

    @Override
    public Token saveToken(XLoginUser xLoginUser) {
        String phone = xLoginUser.getPhone();
        String uuid = phone + "-" + UUID.randomUUID().toString();
        //查询已登录的同一个用户是否存在
        Set<String> keys = redisTemplate.keys("tokens:" + phone + "-*");
        for (String k : keys) {
            //已经有同一个账号的用户登录了，则删除最先登录的账号的key，重新存入新的token（把原先登录的冲掉）
            redisTemplate.delete(k);
        }

        xLoginUser.setToken(uuid);
        String jwtToken = createJWTToken(xLoginUser);
        xLoginUser.setUserToken(jwtToken);
        cacheLoginUser(xLoginUser);

        return new Token(jwtToken, xLoginUser.getLoginTime());
    }

    /**
     * 生成jwt
     *
     * @param loginUser
     * @return
     */
    private String createJWTToken(XLoginUser xLoginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(LOGIN_USER_KEY, xLoginUser.getToken());// 放入一个随机字符串，通过该串可找到登陆用户

        String jwtToken = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, getKeyInstance())
                .compact();

        return jwtToken;
    }

    private void cacheLoginUser(XLoginUser xLoginUser) {
        xLoginUser.setLoginTime(System.currentTimeMillis());
        xLoginUser.setExpireTime(xLoginUser.getLoginTime() + expireSeconds * 1000);
        // 根据uuid将xloginUser缓存
        redisTemplate.boundValueOps(getTokenKey(xLoginUser.getToken())).set(xLoginUser, expireSeconds * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 更新缓存的用户信息
     */
    @Override
    public void refresh(XLoginUser xLoginUser) {
        cacheLoginUser(xLoginUser);
    }

    @Override
    public XLoginUser getLoginUser(String jwtToken) {
        String uuid = getUUIDFromJWT(jwtToken);
        if (uuid != null) {
            return redisTemplate.boundValueOps(getTokenKey(uuid)).get();
        }

        return null;
    }

    @Override
    public boolean deleteToken(String jwtToken) {
        String uuid = getUUIDFromJWT(jwtToken);
        if (uuid != null) {
            String key = getTokenKey(uuid);
            XLoginUser xLoginUser = redisTemplate.opsForValue().get(key);
            if (xLoginUser != null) {
                redisTemplate.delete(key);

                return true;
            }
        }

        return false;
    }

    private String getTokenKey(String uuid) {
        return "tokens:" + uuid;
    }

    private Key getKeyInstance() {
        if (KEY == null) {
            synchronized (XTokenServiceJWTImpl.class) {
                if (KEY == null) {// 双重锁
                    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
                    KEY = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
                }
            }
        }

        return KEY;
    }

    private String getUUIDFromJWT(String jwtToken) {
        if ("null".equals(jwtToken) || StringUtils.isBlank(jwtToken)) {
            return null;
        }

        try {
            Map<String, Object> jwtClaims = Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken).getBody();
            return MapUtils.getString(jwtClaims, LOGIN_USER_KEY);
        } catch (ExpiredJwtException e) {
            log.error("{}已过期", jwtToken);
        } catch (Exception e) {
            log.error("{}", e);
        }

        return null;
    }
}
