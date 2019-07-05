package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.MsgLinkData;
import com.ivay.ivay_app.dto.NoticeMsg;

public interface XFirebaseNoticeService {

    boolean sendAuditNotice();

    boolean sendLoanNotice();

    boolean sendRepaymentNotice();

    boolean sendOverDueNotice();

    MsgLinkData getLinkData(String key);

    String testSendMsg(NoticeMsg msg, String type) throws Exception;
}
