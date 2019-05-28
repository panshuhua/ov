package com.ivay.ivay_app.service;

import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.*;

public interface XUserInfoService {
    /**
     * 根据id获取信息
     *
     * @param gid
     * @return
     */
    XUserInfo getByGid(String gid);

    /**
     * 编辑用户信息
     *
     * @param xUserInfo
     */
    XUserInfo update(XUserInfo xUserInfo);

    /**
     * 根据gid删除
     *
     * @param gid
     * @return
     */
    void delete(String gid);

    /**
     * 获取授信额度
     *
     * @param gid
     * @return
     */
    CreditLine getCreditLine(String gid);

    /**
     * 获取授信认证信息状态
     *
     * @param gid
     * @return
     */
    String getUserStatus(String gid);

    /**
     * 更新用户状态
     */
    int updateUserStatus(String gid, String status);

    /**
     * 提交审核
     *
     * @param gid
     * @return
     */
    int submit(String gid);

    /**
     * 审核
     *
     * @param gid
     * @param flag
     * @return
     */
    int approve(String gid, int flag);


    /**
     * 判断交易密码是否为空
     *
     * @return
     */
    boolean hasTransPwd(String gid);

    /**
     * 设置交易密码
     */
    void setTransPwd(String userGid, String transPwd);

    /**
     * 获取审核记录列表
     *
     * @param limit
     * @param num
     * @param xAuditCondition
     * @return
     */
    PageTableResponse auditList(int limit, int num, XAuditCondition xAuditCondition);

    /**
     * 被审核人详细信息
     *
     * @param userGid
     * @return
     */
    XAuditDetail auditDetail(String userGid);

    /**
     * 提交审核
     *
     * @param userGid
     * @return
     */
    int auditUpdate(String userGid, int flag);

    /**
     * 校验身份证号
     *
     * @param gid
     * @return
     */
    VerifyCodeInfo checkIdentify(String gid, String idCard);

}

