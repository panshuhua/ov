package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.MsgLinkData;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XUserInfo;

public interface XFirebaseNoticeService {

    boolean sendRepaymentNotice();

    boolean sendOverDueNotice();

    MsgLinkData getLinkData(String key);

    String testSendMsg(NoticeMsg msg, String type) throws Exception;

    void sendFirebaseNoticeMsg(NoticeMsg msg) throws Exception;

    void sendPhoneNoticeMsg(NoticeMsg msg) throws Exception;

    NoticeMsg prepareParam(XRecordLoan xRecordLoan, XUserInfo xUserInfo);

    void sendAllNotice(NoticeMsg msg);
}
