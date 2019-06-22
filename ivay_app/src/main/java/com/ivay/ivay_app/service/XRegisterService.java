package com.ivay.ivay_app.service;

import com.ivay.ivay_repository.dto.XUser;
import com.ivay.ivay_repository.model.LoginInfo;
import com.ivay.ivay_repository.model.ReturnUser;
import com.ivay.ivay_repository.dto.VerifyCodeInfo;
import com.ivay.ivay_repository.model.XUserInfo;
import org.tempuri.ApiBulkReturn;

import java.util.Map;

public interface XRegisterService {
    /**
     * 登陆成功生成token
     *
     * @return
     */
    XUser getToken(XUser xUser);

    /**
     * 校验手机号与验证码
     *
     * @param key
     * @param value
     * @return
     */
    boolean checkCode(String key, String value);

    /**
     * 查询mobile对应的userGid
     *
     * @param mobile
     * @return
     */
    String getUserGid(String mobile);

    /**
     * 注册
     *
     * @param password
     * @return
     */
    XUserInfo addUser(LoginInfo loginInfo);

    /**
     * 登录
     *
     * @param xUser
     * @return
     */
    XUser login(XUser xUser);

    /**
     * 注册-自动登录
     *
     * @param mobile
     * @param password
     * @return
     */
    XUser registerLogin(LoginInfo loginInfo);

    /**
     * 根据手机号码修改密码
     *
     * @param mobile
     * @param password
     * @return
     */
    int updatePassword(String userGid, String mobile, String password);

    /**
     * 调用第三方接口发送手机验证码
     *
     * @param mobile
     * @return
     */
    VerifyCodeInfo sendPhoneMsg(String mobile);
    
    //调用接口1发送短信
    Map<String, String> sendMsgBySMS(String mobile, String authCode);

    //调用接口2发送短信
    ApiBulkReturn sendMsgByVMG(String mobile, String authCode);
     
    VerifyCodeInfo sendRegisterCode(int optType,String mobile);
     
    ReturnUser register(LoginInfo loginInfo);
     
    ReturnUser login(LoginInfo loginInfo);
     
    void logout(String userGid);
    
    void resetPwd(String mobile,String verifyCode,String password);

}
