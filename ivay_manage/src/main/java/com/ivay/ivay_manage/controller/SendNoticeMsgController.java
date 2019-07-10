package com.ivay.ivay_manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_common.dto.MsgLinkData;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_manage.service.XFirebaseNoticeService;

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

}
