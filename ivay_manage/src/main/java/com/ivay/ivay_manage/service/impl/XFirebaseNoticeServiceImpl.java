package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.config.SendMsgService;
import com.ivay.ivay_common.dto.MsgLinkData;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_common.dto.SMSResponseStatus;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.service.XConfigService;
import com.ivay.ivay_manage.service.XFirebaseNoticeService;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.XUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class XFirebaseNoticeServiceImpl implements XFirebaseNoticeService {
    private static final Logger logger = LoggerFactory.getLogger(XFirebaseNoticeService.class);

    @Autowired
    XUserInfoDao xUserInfoDao;
    @Autowired
    I18nService i18nService;
    @Autowired
    XConfigService xConfigService;
    @Autowired
    XRecordLoanDao xRecordLoanDao;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    SendMsgService sendMsgService;

    @Value("${noticemsg.effectiveTime}")
    private long effectiveTime;

    @Override
    public MsgLinkData getLinkData(String key) {
        String dataJson = (String) redisTemplate.opsForValue().get(key);
        MsgLinkData data = JsonUtils.jsonToPojo(dataJson, MsgLinkData.class);
        return data;
    }

    // 发送firebase消息推送
    @Override
    public void sendFirebaseNoticeMsg(NoticeMsg msg) throws Exception {
        if (!StringUtils.isEmpty(msg.getFmcToken())) {
            sendMsgService.sendFirebaseNoticeMsg(msg);
        }
    }

    // 发送手机短信
    @Override
    public void sendPhoneNoticeMsg(NoticeMsg msg) throws Exception {
        sendMsgService.sendPhoneNoticeMsg(msg);
        sendPhoneNotice(msg);
    }

    // 发送两种通知
    @Override
    public void sendAllNotice(NoticeMsg msg) {
        try {
            sendPhoneNoticeMsg(msg);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        try {
            sendFirebaseNoticeMsg(msg);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

    }

    // 通过手机短信的方式发送通知
    private boolean sendPhoneNotice(NoticeMsg msg) {
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_SEND_PHONEMSG));
        if (config == null) {
            logger.error("发送短信验证码配置获取出错");
        }

        MsgLinkData data = new MsgLinkData();
        if (msg.getGid() != null) {
            data.setGid(msg.getGid());
        }
        data.setTo(msg.getPageId());
        data.setUserGid(msg.getUserGid());
        String dataJson = JsonUtils.objectToJson(data);

        for (Object key : config.keySet()) {
            String value = config.get(key).toString();

            // 使用方法一发送短信验证码
            if (SysVariable.SMS_ONE.equals(value)) {
                Map<String, String> msgMap = sendMsgService.sendMsgBySMS(msg.getPhone(), msg.getPhoneMsg());
                String status = msgMap.get("status");
                logger.info("SMG方式发送短信验证码返回状态，返回码：{}", status);
                if (SMSResponseStatus.SUCCESS.getCode().equals(status)) {
                    String messageid = msgMap.get("messageid");
                    logger.info("发送的短信id：" + messageid);
                    logger.info("SMG成功发送的内容是：" + msg);
                    // 把需要跳转存储的数据放入redis中
                    if (!StringUtils.isEmpty(msg.getKey())) {
                        redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                    }

                    return true;
                }

            } else if (SysVariable.SMS_TWO.equals(value)) {
                String responseBody = "";
                Map<String, Object> map = null;
                try {
                    responseBody = sendMsgService.sendMsgByFpt(msg.getPhone(), msg.getPhoneMsg());
                    map = JsonUtils.jsonToMap(responseBody);
                    String messageId = (String) map.get("MessageId");
                    logger.info("fpt方式发送的短信id：" + messageId);
                    if (messageId != null) {
                        logger.info("Fpt方式发送的短信验证码是：" + msg);

                        if (!StringUtils.isEmpty(msg.getKey())) {
                            redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                        }

                        return true;
                    } else {
                        logger.info("fpt发送短信失败，返回的错误码为：" + map.get("error") + "，错误信息：" + map.get("error_description"));
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                // 不发送短信验证码，直接返回随机数（把msg1和msg2都修改为0即可）
            } else if (SysVariable.SMS_ZERO.equals(value)) {
                if (!StringUtils.isEmpty(msg.getKey())) {
                    redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                }

                return true;
            }

        }

        return false;

    }

    /**
     * creditLine=0时初始化，>0提额次数：发送初始化额度和提额短信通知
     * @param xUserInfo
     */
    @Override
    public void sendGetCreditLine(XUserInfo xUserInfo) {
        NoticeMsg msg = new NoticeMsg();
        // firebase消息推送参数
        msg.setFmcToken(xUserInfo.getFmcToken());
        // 两种消息共用参数
        msg.setUserGid(xUserInfo.getUserGid());
        msg.setPageId(SysVariable.PAGE_HOME);
        // 手机短信参数
        msg.setPhone(xUserInfo.getPhone());
        String key = MsgAuthCode.getNumBigCharRandom(6);
        msg.setKey(key);
        String url = SysVariable.PHONEMSG_PREFIX_LINK + msg.getKey();

        // 首次获取额度
        if (xUserInfo.getCreditLineCount() == 0) {
            String title = i18nService.getViMessage("firebase.notice.getcredits.remind.titlemsg");
            title = StringUtil.vietnameseToEnglish(title);
            msg.setTitle(title);
            String firebaseMsg = i18nService.getViMessage("firebase.notice.getcredits.remind.msg");
            firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
            logger.info("首次获取额度-发送的firebase推送消息为:{}", firebaseMsg);
            msg.setFirebaseMsg(firebaseMsg);
            String phoneMsg = i18nService.getViMessage("firebase.notice.getcredits.remind.phonemsg");
            phoneMsg = StringUtil.vietnameseToEnglish(phoneMsg);
            phoneMsg = MessageFormat.format(phoneMsg, url);
            logger.info("首次获取额度-发送的手机短信消息为:{}", phoneMsg);
            msg.setPhoneMsg(phoneMsg);
            // 额度变更
        } else if (xUserInfo.getCreditLineCount() > 0) {
            String title = i18nService.getViMessage("firebase.notice.creditschange.remind.titlemsg");
            title = StringUtil.vietnameseToEnglish(title);
            msg.setTitle(title);
            String firebaseMsg = i18nService.getViMessage("firebase.notice.creditschange.remind.msg");
            firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
            long creditLine = xUserInfo.getCreditLine();
            firebaseMsg = MessageFormat.format(firebaseMsg, creditLine).replace(",", ".");
            logger.info("额度变更-发送的firebase推送消息为:{}", firebaseMsg);
            msg.setFirebaseMsg(firebaseMsg);
            String phoneMsg = i18nService.getViMessage("firebase.notice.creditschange.remind.phonemsg");
            phoneMsg = StringUtil.vietnameseToEnglish(phoneMsg);
            phoneMsg = MessageFormat.format(phoneMsg, creditLine, url).replace(",", ".");
            logger.info("额度变更-发送的手机短信消息为:{}", phoneMsg);
            msg.setPhoneMsg(phoneMsg);
        }

        logger.info("发送消息所有参数：" + msg.toString());
        // 发送
        sendAllNotice(msg);

    }

    // 风控借款规则校验失败时，发送借款申请失败通知
    @Override
    public void sendLoanFail(String userGid) {
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        NoticeMsg msg = new NoticeMsg();
        // firebase消息推送参数
        msg.setFmcToken(xUserInfo.getFmcToken());
        // 两种消息共用参数
        msg.setUserGid(userGid);
        // 手机短信参数
        msg.setPhone(xUserInfo.getPhone());

        // 发送内容
        String title = i18nService.getViMessage("firebase.notice.loanfail.remind.titlemsg");
        title = StringUtil.vietnameseToEnglish(title);
        msg.setTitle(title);
        String firebaseMsg = i18nService.getViMessage("firebase.notice.loanfail.remind.msg");
        firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
        logger.info("借款申请失败-发送的firebase推送消息为:{}", firebaseMsg);
        msg.setFirebaseMsg(firebaseMsg);
        String phoneMsg = i18nService.getViMessage("firebase.notice.loanfail.remind.phonemsg");
        phoneMsg = StringUtil.vietnameseToEnglish(phoneMsg);
        logger.info("借款申请失败-发送的手机短信消息为:{}", phoneMsg);
        msg.setPhoneMsg(phoneMsg);

        logger.info("发送消息所有参数：" + msg.toString());
        // 发送
        sendAllNotice(msg);

    }

    @Override
    public void sendManualAuditRjection(XUserInfo xUserInfo) {
        NoticeMsg msg = new NoticeMsg();
        // firebase消息推送参数
        msg.setFmcToken(xUserInfo.getFmcToken());
        // 两种消息共用参数
        msg.setUserGid(xUserInfo.getUserGid());
        msg.setPageId(SysVariable.PAGE_HOME);
        // 手机短信参数
        msg.setPhone(xUserInfo.getPhone());
        String key = MsgAuthCode.getNumBigCharRandom(6);
        msg.setKey(key);
        String url = SysVariable.PHONEMSG_PREFIX_LINK + msg.getKey();

        // 发送内容
        String title = i18nService.getViMessage("firebase.notice.resubmit.remind.titlemsg");
        title = StringUtil.vietnameseToEnglish(title);
        msg.setTitle(title);
        String firebaseMsg = i18nService.getViMessage("firebase.notice.resubmit.remind.msg");
        firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
        logger.info("人工审核（拒绝后）-发送的firebase推送消息为:{}", firebaseMsg);
        msg.setFirebaseMsg(firebaseMsg);
        String phoneMsg = i18nService.getViMessage("firebase.notice.resubmit.remind.phonemsg");
        phoneMsg = StringUtil.vietnameseToEnglish(phoneMsg);
        phoneMsg = MessageFormat.format(phoneMsg, url);
        logger.info("人工审核（拒绝后）-发送的手机短信消息为:{}", phoneMsg);
        msg.setPhoneMsg(phoneMsg);

        logger.info("发送消息所有参数：" + msg.toString());
        // 发送
        sendAllNotice(msg);

    }

}
