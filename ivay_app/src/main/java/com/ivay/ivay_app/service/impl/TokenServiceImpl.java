package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dto.LoginUser;
import com.ivay.ivay_app.dto.Token;
import com.ivay.ivay_app.service.SysLogService;
import com.ivay.ivay_app.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token存到redis的实现类<br>
 * 普通token，uuid
 * 
 * @author xx
 *
 */
@Deprecated
//@Service
public class TokenServiceImpl implements TokenService {

	/**
	 * token过期秒数
	 */
	@Value("${token.expire.seconds}")
	private Integer expireSeconds;
	@Autowired
	private RedisTemplate<String, LoginUser> redisTemplate;
	@Autowired
	private SysLogService logService;

	@Override
	public Token saveToken(LoginUser loginUser) {
		String token = UUID.randomUUID().toString();

		loginUser.setToken(token);
		cacheLoginUser(loginUser);

		return new Token(token, loginUser.getLoginTime());
	}

	private void cacheLoginUser(LoginUser loginUser) {
		loginUser.setLoginTime(System.currentTimeMillis());
		loginUser.setExpireTime(loginUser.getLoginTime() + expireSeconds * 1000);
		// 缓存
		redisTemplate.boundValueOps(getTokenKey(loginUser.getToken())).set(loginUser, expireSeconds, TimeUnit.SECONDS);
	}

	/**
	 * 更新缓存的用户信息
	 */
	@Override
	public void refresh(LoginUser loginUser) {
		cacheLoginUser(loginUser);
	}

	@Override
	public LoginUser getLoginUser(String token) {
		return redisTemplate.boundValueOps(getTokenKey(token)).get();
	}

	@Override
	public boolean deleteToken(String token) {
		String key = getTokenKey(token);
		LoginUser loginUser = redisTemplate.opsForValue().get(key);
		if (loginUser != null) {
			redisTemplate.delete(key);

			return true;
		}

		return false;
	}

	private String getTokenKey(String token) {
		return "tokens:" + token;
	}

}
