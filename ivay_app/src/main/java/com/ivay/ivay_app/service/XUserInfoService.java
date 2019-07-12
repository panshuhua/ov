package com.ivay.ivay_app.service;


import com.ivay.ivay_repository.dto.VerifyCodeInfo;
import com.ivay.ivay_repository.model.XUserInfo;

public interface XUserInfoService {
    /**
     * 编辑用户信息
     *
     * @param xUserInfo
     * @return
     */
    XUserInfo update(XUserInfo xUserInfo);

    /**
     * 更新用户状态
     *
     * @param gid
     * @param status
     * @return
     */
    int updateUserStatus(String gid, String status);

    /**
     * 确认提交授信信息
     *
     * @param gid
     * @return
     */
    int submit(String gid);

    /**
     * 判断交易密码是否为空
     *
     * @param gid
     * @return
     */
    boolean hasTransPwd(String gid);

    /**
     * 设置交易密码
     *
     * @param userGid
     * @param transPwd
     * @return
     */
    void setTransPwd(String userGid, String transPwd);

    /**
     * 校验身份证号
     *
     * @param gid
     * @param idCard
     * @return
     */
    VerifyCodeInfo checkIdentify(String gid, String idCard);


    /**
     * 校验该设备是不是被注册过了
     *
     * @param macCode
     * @return
     */
    String checkMacCode(String macCode);
}

