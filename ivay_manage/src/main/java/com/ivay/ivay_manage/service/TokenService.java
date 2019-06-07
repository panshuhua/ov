package com.ivay.ivay_manage.service;

import com.ivay.ivay_manage.dto.LoginUser;
import com.ivay.ivay_manage.dto.Token;

/**
 * Token管理器<br>
 * 可存储到redis或者数据库<br>
 * 具体可看实现类<br>
 * 默认基于redis，实现类为 TokenServiceJWTImpl<br>
 * 如要换成数据库存储，将TokenServiceImpl类上的注解@Primary挪到com.ivay.ivay_manage.service.impl.TokenServiceDbImpl
 *
 * @author xx
 * <p>
 * 2017年10月14日
 */
public interface TokenService {

    Token saveToken(LoginUser loginUser);

    void refresh(LoginUser loginUser);

    LoginUser getLoginUser(String token);

    boolean deleteToken(String token);

}
