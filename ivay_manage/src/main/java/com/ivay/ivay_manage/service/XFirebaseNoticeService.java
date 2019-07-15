package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.dto.MsgLinkData;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_repository.model.XUserInfo;

public interface XFirebaseNoticeService {

    /**
     * 获取短链接中的参数
     * 
     * @param key
     * @return
     */
    MsgLinkData getLinkData(String key);

    /**
     * 发送firebse消息推送
     * 
     * @param msg
     * @throws Exception
     */
    void sendFirebaseNoticeMsg(NoticeMsg msg) throws Exception;

    /**
     * 发送手机短信
     * 
     * @param msg
     * @throws Exception
     */
    void sendPhoneNoticeMsg(NoticeMsg msg) throws Exception;

    /**
     * 发送通知
     * 
     * @param msg
     */
    void sendAllNotice(NoticeMsg msg);

    /**
     * 发送借款失败通知：借款申请失败
     */
    void sendLoanFail(String userGid);

    /**
     * 发送获额通知：首次获得额度/额度变更
     * 
     * @param xUserInfo
     */
    void sendGetCreditLine(XUserInfo xUserInfo);

    /**
     * 人工审核拒绝后通知
     * 
     * @param xUserInfo
     */
    void sendManualAuditRjection(XUserInfo xUserInfo);

}
