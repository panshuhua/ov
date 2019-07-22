package com.ivay.ivay_manage.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.config.SendMsgService;
import com.ivay.ivay_common.dto.MsgLinkData;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_common.dto.SMSResponseStatus;
import com.ivay.ivay_common.utils.FirebaseUtil;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.service.XConfigService;
import com.ivay.ivay_manage.service.XFirebaseNoticeService;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.XUserInfo;

@Service
public class XFirebaseNoticeServiceImpl implements XFirebaseNoticeService {
    private static final Logger logger = LoggerFactory.getLogger(XFirebaseNoticeServiceImpl.class);

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
    @Value("${spring.profiles.include}")
    private String environment;

    @Override
    public MsgLinkData getLinkData(String key) {
        String dataJson = (String)redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(dataJson)) {
            MsgLinkData data = JsonUtils.jsonToPojo(dataJson, MsgLinkData.class);
            return data;
        }

        return null;
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
    public void sendAllNotice(NoticeMsg msg, boolean flag) {
        logger.info("进入实际发送方法-------------userGid=" + msg.getUserGid() + "-------------");

        try {
            // 两种一起发送
            // sendPhoneNoticeMsg(msg);

            // flag=true表示是敏感信息
            if (flag) {
                sendPhoneNoticeByPas(msg); // 敏感信息用国内平台发
            } else {
                // sendPhoneNoticeByFpt(msg); // 非敏感信息用fpt平台发
                sendPhoneNotice2(msg); // TODO
            }

            logger.info("发送首次获取额度/额度变更/借款风控失败-------短信成功-----------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sendFirebaseNoticeMsg(msg);
            logger.info("发送首次获取额度/额度变更/借款风控失败---------firebase通知成功-----------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 通过手机短信的方式发送通知-两种方式一起发，优先越南fpt平台
    private boolean sendPhoneNotice2(NoticeMsg msg) {
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

        Set<String> keySet = config.keySet();
        List<String> keyList = new ArrayList<String>(keySet);
        Collections.sort(keyList); // 排序：控制优先使用msg1配置项发送
        Iterator<String> iter = keyList.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = (String)config.get(key);

            boolean flag = sendMsgByManyTypes(msg, value, dataJson);
            if (!flag) {
                continue;
            }
            return true;
        }

        return false;
    }

    /**
     * 使用多种方式发送短信验证码
     */
    private boolean sendMsgByManyTypes(NoticeMsg msg, String value, String dataJson) {
        String phone = msg.getPhone();
        String phoneMsg = msg.getPhoneMsg();
        // 使用方法一发送短信验证码：只要是10位数字的手机号码都不会报错
        if (SysVariable.SMS_ONE.equals(value)) {
            Map<String, String> msgMap = sendMsgService.sendMsgBySMS(SysVariable.SMS_TYPE_NOTICE, phone, phoneMsg);
            String status = msgMap.get("status");
            logger.info("pasoo方式发送短信验证码返回状态，返回码：{}", status);
            if (SMSResponseStatus.SUCCESS.getCode().equals(status)) {
                String messageid = msgMap.get("messageid");
                logger.info("发送的短信id：" + messageid);
                logger.info("pasoo成功发送的内容是：" + msg);
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
                responseBody = sendMsgService.sendMsgByFpt(phone, phoneMsg);
                map = JsonUtils.jsonToMap(responseBody);
                String messageId = (String)map.get("MessageId");
                logger.info("fpt方式发送的短信id：" + messageId);
                if (messageId != null) {
                    logger.info("Fpt方式发送的短信是:{}", phoneMsg);
                    if (!StringUtils.isEmpty(msg.getKey())) {
                        redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                    }
                    return true;
                } else {
                    logger.info("fpt发送短信失败，返回的错误码为：" + map.get("error") + "，错误信息：" + map.get("error_description"));
                    return false;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }

        } else if (SysVariable.SMS_ZERO.equals(value)) {
            if (!StringUtils.isEmpty(msg.getKey())) {
                redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
            }
            return true;
        }

        return false;

    }

    /**
     * 国内平台发短信-涉及到金额的敏感短信用国内平台发
     * 
     * @param msg
     * @return
     */
    private boolean sendPhoneNoticeByPas(NoticeMsg msg) {
        MsgLinkData data = new MsgLinkData();
        if (msg.getGid() != null) {
            data.setGid(msg.getGid());
        }
        data.setTo(msg.getPageId());
        data.setUserGid(msg.getUserGid());
        String dataJson = JsonUtils.objectToJson(data);

        logger.info("所属环境：" + environment);
        // 生产环境才真正发送短信
        if (environment.contains("prod")) {
            Map<String, String> msgMap =
                sendMsgService.sendMsgBySMS(SysVariable.SMS_TYPE_NOTICE, msg.getPhone(), msg.getPhoneMsg());
            String status = msgMap.get("status");
            logger.info("pas方式发送短信验证码返回状态，返回码：{}", status);
            if (SMSResponseStatus.SUCCESS.getCode().equals(status)) {
                String messageid = msgMap.get("messageid");
                logger.info("发送的短信id：" + messageid);
                logger.info("pas成功发送的内容是：" + msg);
                // 把需要跳转存储的数据放入redis中
                if (!StringUtils.isEmpty(msg.getKey())) {
                    redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                }

                return true;
            }

            // 测试/开发环境直接保存信息
        } else {
            if (!StringUtils.isEmpty(msg.getKey())) {
                redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
            }
            return true;
        }

        return false;
    }

    /**
     * 越南fpt平台发送非敏感短信
     * 
     * @param msg
     * @return
     */
    private boolean sendPhoneNoticeByFpt(NoticeMsg msg) {
        MsgLinkData data = new MsgLinkData();
        if (msg.getGid() != null) {
            data.setGid(msg.getGid());
        }
        data.setTo(msg.getPageId());
        data.setUserGid(msg.getUserGid());
        String dataJson = JsonUtils.objectToJson(data);

        String responseBody = "";
        Map<String, Object> map = null;
        try {
            responseBody = sendMsgService.sendMsgByFpt(msg.getPhone(), msg.getPhoneMsg());
            map = JsonUtils.jsonToMap(responseBody);
            String messageId = (String)map.get("MessageId");
            logger.info("fpt方式发送的短信id：" + messageId);
            if (messageId != null) {
                logger.info("Fpt方式发送的短信验证码是：" + msg);

                if (!StringUtils.isEmpty(msg.getKey())) {
                    redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                }

                return true;
            } else {
                logger.info("fpt发送短信失败，返回的错误码为：" + map.get("error") + "，错误信息：" + map.get("error_description"));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    // 通过手机短信的方式发送通知：优先国内平台发，可以两个平台一起发
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
                Map<String, String> msgMap =
                    sendMsgService.sendMsgBySMS(SysVariable.SMS_TYPE_NOTICE, msg.getPhone(), msg.getPhoneMsg());
                String status = msgMap.get("status");
                logger.info("pas方式发送短信验证码返回状态，返回码：{}", status);
                if (SMSResponseStatus.SUCCESS.getCode().equals(status)) {
                    String messageid = msgMap.get("messageid");
                    logger.info("发送的短信id：" + messageid);
                    logger.info("pas成功发送的内容是：" + msg);
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
                    String messageId = (String)map.get("MessageId");
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
     * 
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

        // 首次获取额度－已发送
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
            sendAllNotice(msg, false);
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
            // TODO
            sendAllNotice(msg, true);
        }

        logger.info("发送消息所有参数：" + msg.toString());

    }

    // 风控借款规则校验失败时，发送借款申请失败通知
    // @Override
    // public void sendLoanFail(String userGid) {
    // XUserInfo xUserInfo = xUserInfoDao.getByUserGid(userGid);
    // NoticeMsg msg = new NoticeMsg();
    // // firebase消息推送参数
    // msg.setFmcToken(xUserInfo.getFmcToken());
    // // 两种消息共用参数
    // msg.setUserGid(userGid);
    // // 手机短信参数
    // msg.setPhone(xUserInfo.getPhone());
    //
    // // 发送内容
    // String title = i18nService.getViMessage("firebase.notice.loanfail.remind.titlemsg");
    // title = StringUtil.vietnameseToEnglish(title);
    // msg.setTitle(title);
    // String firebaseMsg = i18nService.getViMessage("firebase.notice.loanfail.remind.msg");
    // firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
    // logger.info("借款申请失败-发送的firebase推送消息为:{}", firebaseMsg);
    // msg.setFirebaseMsg(firebaseMsg);
    // String phoneMsg = i18nService.getViMessage("firebase.notice.loanfail.remind.phonemsg");
    // phoneMsg = StringUtil.vietnameseToEnglish(phoneMsg);
    // logger.info("借款申请失败-发送的手机短信消息为:{}", phoneMsg);
    // msg.setPhoneMsg(phoneMsg);
    //
    // logger.info("发送消息所有参数：" + msg.toString());
    // // 发送
    // sendAllNotice(msg, false);
    //
    // }

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
        sendAllNotice(msg, false);
    }

    @Override
    public String testSendMsg(NoticeMsg msg, String type) throws Exception {
        if (SysVariable.NOTICE_FIREBASE_MSG.equals(type)) {
            String firebaseTitle = msg.getTitle();
            String firebaseMsg = msg.getFirebaseMsg();
            firebaseTitle = StringUtil.vietnameseToEnglish(firebaseTitle);
            firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
            msg.setTitle(firebaseTitle);
            msg.setFirebaseMsg(firebaseMsg);
            logger.info("firebase推送标题：" + msg.getTitle());
            logger.info("firebase推送内容：" + msg.getFirebaseMsg());
            logger.info("firebase跳转页面to：" + msg.getPageId());
            logger.info("firebase借款gid：" + msg.getGid());
            logger.info("firebase用户gid：" + msg.getUserGid());
            FirebaseUtil.sendMsgToFmcToken(msg);
        } else if (SysVariable.NOTICE_PHONE_MSG.equals(type)) {
            String key = MsgAuthCode.getNumBigCharRandom(6);
            String phoneMsg = StringUtil.vietnameseToEnglish(msg.getPhoneMsg());
            phoneMsg = phoneMsg + " " + SysVariable.PHONEMSG_PREFIX_LINK + key;
            msg.setPhoneMsg(phoneMsg);
            msg.setKey(key);
            logger.info("短信页面短链接的key：" + msg.getKey());
            logger.info("手机短信发送的电话号码：" + msg.getPhone());
            logger.info("手机短信发送内容：" + msg.getPhoneMsg());
            logger.info("手机短信跳转页面：" + msg.getPageId());
            logger.info("手机短信借款gid：" + msg.getGid());
            logger.info("手机短信用户gid：" + msg.getUserGid());
            sendPhoneNotice(msg);
            return key;
        }

        return null;
    }

}
