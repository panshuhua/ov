package com.ivay.ivay_app.service;

import com.ivay.ivay_common.dto.MsgLinkData;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XRecordRepayment;
import com.ivay.ivay_repository.model.XUserInfo;

public interface XFirebaseNoticeService {

    boolean sendRepaymentNotice();

    boolean sendOverDueNotice();

    boolean sendRepaymentNotice1();

    boolean sendOverDueNotice1();

    MsgLinkData getLinkData(String key);

    String testSendMsg(NoticeMsg msg, String type) throws Exception;

    void sendFirebaseNoticeMsg(NoticeMsg msg) throws Exception;

    void sendPhoneNoticeMsg(NoticeMsg msg) throws Exception;

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
