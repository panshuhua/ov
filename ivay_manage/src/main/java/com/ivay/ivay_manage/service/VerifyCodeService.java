package com.ivay.ivay_manage.service;

/**
 * 获取短信验证码
 * 
 * @author panshuhua
 * @date 2019/07/10
 */
public interface VerifyCodeService {

    /**
     * 获取短信验证码
     * 
     * @param phone
     * @return
     */
    String getVerifyCode(String phone);

}
