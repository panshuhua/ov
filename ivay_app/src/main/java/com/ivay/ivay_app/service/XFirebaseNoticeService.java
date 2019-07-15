package com.ivay.ivay_app.service;

import com.ivay.ivay_common.dto.MsgLinkData;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XRecordRepayment;
import com.ivay.ivay_repository.model.XUserInfo;

public interface XFirebaseNoticeService {

    /**
     * 发送当天/第二天到期还款通知
     * 
     * @return
     */
    boolean sendRepaymentNotice();

    /**
     * 发送逾期通知
     * 
     * @return
     */
    boolean sendOverDueNotice();

    boolean sendRepaymentNotice1();

    boolean sendOverDueNotice1();

    /**
     * 获取短链接包含的参数
     * 
     * @param key
     * @return
     */
    MsgLinkData getLinkData(String key);

    /**
     * 测试发送各种类型的消息推送和短信
     * 
     * @param msg
     * @param type
     * @return
     * @throws Exception
     */
    String testSendMsg(NoticeMsg msg, String type) throws Exception;

    /**
     * 发送firebase消息推送
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
     * 发送已还款通知：全部还款与部分还款
     */
    void sendHadRepayNotice(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment, XUserInfo xUserInfo);

    /**
     * 发送借款通知：借款成功/失败
     * 
     * @param xRecordLoan
     */
    void sendLoanSuccessNotice(XRecordLoan xRecordLoan, XUserInfo xUserInfo);
}
