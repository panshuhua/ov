package com.ivay.ivay_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_app.service.XFirebaseNoticeService;
import com.ivay.ivay_common.dto.MsgLinkData;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_common.dto.Response;

import io.swagger.annotations.Api;

/**
 * 发送推送/短信消息测试
 * 
 * @author panshuhua
 * @date 2019/07/03
 */
@RestController
@RequestMapping("star/sendNoticeMsg")
@Api(tags = "发送短信/推送通知消息")
public class SendNoticeMsgController {
    @Autowired
    XFirebaseNoticeService xFirebaseNoticeService;

    /**
     * 当天到期/第二天到期消息发送
     */
    @GetMapping("/testRepaymentNotice")
    public void testRepaymentNotice() {
        boolean flag = xFirebaseNoticeService.sendRepaymentNotice();
        System.out.println(flag);
    }

    /**
     * 逾期消息发送
     */
    @GetMapping("/testOverDueNotice")
    public void testOverDueNotice() {
        boolean flag = xFirebaseNoticeService.sendOverDueNotice();
        System.out.println(flag);
    }

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
        response.setBo(xFirebaseNoticeService.testSendMsg(msg, type));
        return response;
    }

}
