package com.ivay.ivay_manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_common.dto.MsgLinkData;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_manage.service.XFirebaseNoticeService;
import com.ivay.ivay_repository.model.XUserInfo;

import io.swagger.annotations.Api;

/**
 * 发送推送/短信消息测试
 * 
 * @author panshuhua
 * @date 2019/07/03
 */
@RestController
@RequestMapping("manage/sendNoticeMsg")
@Api(tags = "发送短信/推送通知消息")
public class SendNoticeMsgController {
    @Autowired
    XFirebaseNoticeService xFirebaseNoticeService;

    /**
     * 获取页面短链接和借款gid
     * 
     * @param pageName
     * @param mobile
     */
    @GetMapping("/getLinkData")
    public Response<MsgLinkData> getLinkData(@RequestParam String key) {
        MsgLinkData data = xFirebaseNoticeService.getLinkData(key);
        Response<MsgLinkData> response = new Response<MsgLinkData>();
        response.setBo(data);
        return response;
    }

    /**
     * 发送推送消息/短信测试 ：type=1 推送消息|type=2 短信
     * 
     * @param pageId
     * @return
     * @throws Exception
     */
    @PostMapping("/testSendMsg")
    public Response<String> testSendMsg(@RequestParam String type, @RequestBody NoticeMsg msg) throws Exception {
        Response<String> response = new Response<String>();
        // response.setBo(xFirebaseNoticeService.testSendMsg(msg, type));
        // 测试-首次获取额度
        /*XUserInfo xUserInfo = new XUserInfo();
        xUserInfo.setFmcToken(
            "e4XxSYJN8fM:APA91bG0BPhiZIcI4Q-JHIp9t5_9zFytXumyfY1EEOgQ9IXbWLZCpD5aPeu1C-oltVp9CLvFALMsgjlJsqkasm5QaqhRIDpn7jcmsudfX4A9JCqkvTdF9lMyoBCbFSyZ93gZ8VhnOP1A");
        xUserInfo.setPhone("0917143991");
        xUserInfo.setCreditLineCount(1);
        xUserInfo.setCreditLine(1000000L);
        xFirebaseNoticeService.sendGetCreditLine(xUserInfo);*/
        // 测试-人工拒绝后重新提交
        XUserInfo xUserInfo = new XUserInfo();
        xUserInfo.setFmcToken(
            "e4XxSYJN8fM:APA91bG0BPhiZIcI4Q-JHIp9t5_9zFytXumyfY1EEOgQ9IXbWLZCpD5aPeu1C-oltVp9CLvFALMsgjlJsqkasm5QaqhRIDpn7jcmsudfX4A9JCqkvTdF9lMyoBCbFSyZ93gZ8VhnOP1A");
        xUserInfo.setPhone("0973497955");
        xFirebaseNoticeService.sendManualAuditRjection(xUserInfo);
        return response;
    }

}
