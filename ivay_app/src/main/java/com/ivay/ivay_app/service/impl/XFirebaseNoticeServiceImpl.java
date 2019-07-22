package com.ivay.ivay_app.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

import com.ivay.ivay_app.service.XConfigService;
import com.ivay.ivay_app.service.XFirebaseNoticeService;
import com.ivay.ivay_app.service.XRegisterService;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.config.SendMsgService;
import com.ivay.ivay_common.dto.MsgLinkData;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_common.dto.SMSResponseStatus;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.FirebaseUtil;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserBankcardInfoDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dto.XOverDueFee;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XRecordRepayment;
import com.ivay.ivay_repository.model.XUserBankcardInfo;
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
    XRegisterService xRegisterService;
    @Autowired
    XRecordLoanDao xRecordLoanDao;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    SendMsgService sendMsgService;
    @Autowired
    private XUserBankcardInfoDao xUserBankcardInfoDao;

    @Value("${noticemsg.effectiveTime}")
    private long effectiveTime;
    @Value("${spring.profiles.include}")
    private String environment;

    @Override
    public boolean sendRepaymentNotice() {
        // 发送到期前一天/当天提醒
        List<XOverDueFee> xOverDueFeeList = xRecordLoanDao.findOneOverdue();
        logger.info("到期前一天/当天提醒的总条数：" + xOverDueFeeList.size() + "-----------------------");
        // firebase消息标题
        String title = "";
        // 推送发送内容
        String message = "";
        // 短信发送内容
        String phoneMsg = "";

        int i = 0;
        for (XOverDueFee fee : xOverDueFeeList) {
            try {
                i++;
                String fmcToken = fee.getFmcToken();
                Date dueTime = fee.getDueTime();
                String dt = DateUtils.dateToString_DD_MM_YYYY(dueTime);
                // 随机生成短链接的key
                String key = MsgAuthCode.getNumBigCharRandom(6);
                String url = SysVariable.PHONEMSG_PREFIX_LINK + key;

                Long dueAmount = fee.getDueAmount();
                if (dueTime.getDate() == new Date().getDate()) {
                    title = i18nService.getViMessage("firebase.notice.dueDay.remind.titlemsg");
                    message = i18nService.getViMessage("firebase.notice.dueDay.remind.msg");
                    phoneMsg = i18nService.getViMessage("firebase.notice.dueDay.remind.phonemsg");
                    message = MessageFormat.format(message, dueAmount);
                    phoneMsg = MessageFormat.format(phoneMsg, dueAmount, url);
                    logger.info("当天到期");
                } else if (dueTime.getDate() == new Date().getDate() + 1) {
                    title = i18nService.getViMessage("firebase.notice.beforeDueDay.remind.titlemsg");
                    message = i18nService.getViMessage("firebase.notice.beforeDueDay.remind.msg");
                    phoneMsg = i18nService.getViMessage("firebase.notice.beforeDueDay.remind.phonemsg");
                    message = MessageFormat.format(message, dueAmount, dt);
                    phoneMsg = MessageFormat.format(phoneMsg, dueAmount, dt, url);
                    logger.info("明天到期");
                }

                message = message.replace(",", ".");
                title = StringUtil.vietnameseToEnglish(title);
                message = StringUtil.vietnameseToEnglish(message);

                phoneMsg = phoneMsg.replace(",", ".");
                phoneMsg = StringUtil.vietnameseToEnglish(phoneMsg);

                logger.info("正在发送第" + i + "条信息----------------");
                logger.info("发送的第" + i + "条到期推送消息为：" + message);
                logger.info("发送的第" + i + "条到期短信消息为：" + phoneMsg);

                boolean firebaseFlag = true;
                // 分开捕获异常，避免其中一种发送方式出问题了，另外一种方式发送不了
                // 发送firebase消息推送
                logger.info("发送firebase到期通知开始-------------");
                String gid = fee.getGid();
                NoticeMsg msg = new NoticeMsg();
                msg.setPageId(SysVariable.PAGE_BILLDETAIL);
                msg.setGid(gid);
                msg.setUserGid(fee.getUserGid());
                logger.info("当天/明天到期通知userGid=" + msg.getUserGid() + "---------------------");
                if (!StringUtils.isEmpty(fmcToken)) {
                    try {
                        msg.setFmcToken(fmcToken);
                        msg.setFirebaseMsg(message);
                        msg.setTitle(title);
                        // TODO
                        FirebaseUtil.sendMsgToFmcToken(msg);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        firebaseFlag = false;
                    }
                }

                logger.info("发送firebase到期通知结束-------------");

                boolean phoneFlag = true;
                // 发送手机短信
                String phone = fee.getPhone();
                try {
                    msg.setPhone(phone);
                    msg.setPhoneMsg(phoneMsg);
                    msg.setKey(key);
                    // TODO
                    // phoneFlag = sendPhoneNotice(msg);
                    phoneFlag = sendPhoneNoticeByPas(msg);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    phoneFlag = false;
                }

                // 推送和短信都出现异常
                if (!firebaseFlag && !phoneFlag) {
                    return false;
                }
                Thread.sleep(10000);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }

        }

        logger.info("发送完毕，总共成功发送" + i + "条");

        return true;

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

    // 通过手机短信的方式发送通知-两种方式一起发，优先国内pas平台
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
                    responseBody = sendMsgService.sendMsgByFpt(msg.getPhone(), msg.getPhoneMsg());
                    map = JsonUtils.jsonToMap(responseBody);
                    String messageId = (String)map.get("MessageId");
                    logger.info("fpt方式发送的短信id：" + messageId);
                    if (messageId != null) {
                        logger.info("Fpt方式发送的短信是:{}", msg.getPhoneMsg());
                        if (!StringUtils.isEmpty(msg.getKey())) {
                            redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                        }
                        return true;
                    } else {
                        logger.info("fpt发送短信失败，返回的错误码为：" + map.get("error") + "，错误信息：" + map.get("error_description"));
                        continue;
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    continue;
                }

                // 不真正发送短信，测试用（把msg1和msg2都修改为0即可）
            } else if (SysVariable.SMS_ZERO.equals(value)) {
                if (!StringUtils.isEmpty(msg.getKey())) {
                    redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                }
                return true;
            }

        }

        return false;

    }

    @Override
    public boolean sendOverDueNotice() {
        // 发送逾期提醒
        List<XOverDueFee> xOverDueFeeList = xRecordLoanDao.findOverdue();
        logger.info("逾期提醒的总条数：" + xOverDueFeeList.size() + "-----------------------");
        // firebase消息标题
        String title = "";
        // 短信发送内容
        String message = "";

        int i = 0;
        for (XOverDueFee fee : xOverDueFeeList) {
            try {
                i++;
                String fmcToken = fee.getFmcToken();
                title = i18nService.getViMessage("firebase.notice.overdue.remind.titlemsg");
                message = i18nService.getViMessage("firebase.notice.overdue.remind.msg");
                Integer dueDate = fee.getDueDate(); // 逾期天数
                long overdueFee = fee.getOverdueFee() + fee.getOverdueInterest(); // 逾期后：逾期费用+逾期利息
                message = MessageFormat.format(message, dueDate, overdueFee);
                title = StringUtil.vietnameseToEnglish(title);
                message = StringUtil.vietnameseToEnglish(message).replace(",", ".");

                logger.info("发送的第" + i + "条逾期消息为：" + message);
                logger.info("正在发送第" + i + "条信息----------------");
                logger.info("发送firebase到期通知开始-------------");

                if (!StringUtils.isEmpty(fmcToken)) {
                    String gid = fee.getGid();
                    String userGid = fee.getUserGid();
                    NoticeMsg msg = new NoticeMsg();
                    msg.setFmcToken(fmcToken);
                    msg.setFirebaseMsg(message);
                    msg.setTitle(title);
                    msg.setPageId(SysVariable.PAGE_BILLDETAIL);
                    msg.setGid(gid);
                    msg.setUserGid(userGid);

                    logger.info("逾期通知userGid=" + userGid + "---------------------");
                    // TODO
                    FirebaseUtil.sendMsgToFmcToken(msg);
                }

                logger.info("发送firebase到期通知结束-------------");

                Thread.sleep(10000);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }

        }

        logger.info("发送完毕，总共成功发送" + i + "条");

        return true;
    }

    @Override
    public MsgLinkData getLinkData(String key) {
        String dataJson = (String)redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(dataJson)) {
            MsgLinkData data = JsonUtils.jsonToPojo(dataJson, MsgLinkData.class);
            return data;
        }

        return null;
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
        // TODO 发正式
        try {
            // 两种一起发送
            // sendPhoneNoticeMsg(msg);

            // flag=true表示是敏感信息
            if (flag) {
                sendPhoneNoticeByPas(msg); // 敏感信息用国内平台发（生产环境才能真正发）
            } else {
                // sendPhoneNoticeByFpt(msg); // 非敏感信息优先用fpt平台发，再使用pasoo访问
                sendPhoneNotice2(msg); // TODO
            }

            logger.info("发送部分/全部还款/借款成功-------短信成功-----------------------");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        try {
            sendFirebaseNoticeMsg(msg);
            logger.info("发送部分/全部还款/借款成功----------firebase通知成功-----------------------");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void sendHadRepayNotice(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment, XUserInfo xUserInfo) {
        logger.info("进入发送还款成功参数准备方法-----------------------");
        logger.info("还款状态:{},用户gid:{},借款gid:{}", xRecordLoan.getRepaymentStatus(), xRecordLoan.getUserGid(),
            xRecordLoan.getGid());
        // TODO 发送还款成功的通知
        NoticeMsg msg = new NoticeMsg();
        // firebase消息推送参数
        msg.setFmcToken(xUserInfo.getFmcToken());
        // 两种消息共用参数
        msg.setGid(xRecordLoan.getGid());
        msg.setUserGid(xRecordLoan.getUserGid());
        msg.setPageId(SysVariable.PAGE_BILLDETAIL);
        // 手机短信参数
        msg.setPhone(xUserInfo.getPhone());
        String key = MsgAuthCode.getNumBigCharRandom(6);
        msg.setKey(key);

        String url = SysVariable.PHONEMSG_PREFIX_LINK + msg.getKey();
        // 应还时间-格式修改为越南日期格式
        String dueTime = DateUtils.dateToString_DD_MM_YYYY(xRecordLoan.getDueTime());

        // 部分还款消息通知
        if (xRecordLoan.getRepaymentStatus() == SysVariable.REPAYMENT_STATUS_DOING) {
            String title = i18nService.getViMessage("firebase.notice.partrepay.remind.titlemsg");
            title = StringUtil.vietnameseToEnglish(title);
            msg.setTitle(title);
            String firebaseMsg = i18nService.getViMessage("firebase.notice.partrepay.remind.msg");
            firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
            firebaseMsg = MessageFormat.format(firebaseMsg, xRecordRepayment.getRepaymentAmount(),
                xRecordLoan.getDueAmount() + xRecordLoan.getOverdueFee(), dueTime).replace(",", ".");
            logger.info("部分还款-发送的firebase推送消息为:{}", firebaseMsg);
            msg.setFirebaseMsg(firebaseMsg);
            String phoneMsg = i18nService.getViMessage("firebase.notice.partrepay.remind.phonemsg");
            phoneMsg = StringUtil.vietnameseToEnglish(phoneMsg);
            phoneMsg = MessageFormat.format(phoneMsg, xRecordRepayment.getRepaymentAmount(),
                xRecordLoan.getDueAmount() + xRecordLoan.getOverdueFee(), dueTime, url).replace(",", ".");
            logger.info("部分还款-发送的手机短信消息为:{}", phoneMsg);
            msg.setPhoneMsg(phoneMsg);
            // 全部还款消息通知
        } else if (xRecordLoan.getRepaymentStatus() == SysVariable.REPAYMENT_STATUS_SUCCESS) {
            String title = i18nService.getViMessage("firebase.notice.allrepay.remind.titlemsg");
            title = StringUtil.vietnameseToEnglish(title);
            msg.setTitle(title);
            String firebaseMsg = i18nService.getViMessage("firebase.notice.allrepay.remind.msg");
            firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
            firebaseMsg = MessageFormat.format(firebaseMsg, xRecordRepayment.getRepaymentAmount()).replace(",", ".");
            logger.info("全部还款-发送的firebase推送消息为:{}", firebaseMsg);
            msg.setFirebaseMsg(firebaseMsg);
            String phoneMsg = i18nService.getViMessage("firebase.notice.allrepay.remind.phonemsg");
            phoneMsg = StringUtil.vietnameseToEnglish(phoneMsg);
            phoneMsg = MessageFormat.format(phoneMsg, xRecordRepayment.getRepaymentAmount(), url).replace(",", ".");
            logger.info("全部还款-发送的手机短信消息为:{}", phoneMsg);
            msg.setPhoneMsg(phoneMsg);
        }

        sendAllNotice(msg, true);

    }

    @Override
    public void sendLoanSuccessNotice(XRecordLoan xRecordLoan, XUserInfo xUserInfo) {
        NoticeMsg msg = new NoticeMsg();
        // firebase消息推送参数
        msg.setFmcToken(xUserInfo.getFmcToken());
        // 两种消息共用参数
        msg.setUserGid(xRecordLoan.getUserGid());
        // 手机短信参数
        msg.setPhone(xUserInfo.getPhone());

        // 借款成功
        if (xRecordLoan.getLoanStatus() == SysVariable.LOAN_STATUS_SUCCESS) {
            String title = i18nService.getViMessage("firebase.notice.loansuccess.remind.titlemsg");
            title = StringUtil.vietnameseToEnglish(title);
            msg.setTitle(title);
            String firebaseMsg = i18nService.getViMessage("firebase.notice.loansuccess.remind.msg");
            List<XUserBankcardInfo> bankCardNoInfoList =
                xUserBankcardInfoDao.getByCardGid(xRecordLoan.getBankcardGid(), xRecordLoan.getUserGid());
            String bankCardNo = "";
            if (bankCardNoInfoList.size() > 0) {
                XUserBankcardInfo bankCardNoInfo = bankCardNoInfoList.get(0);
                bankCardNo = bankCardNoInfo.getCardNo();
                bankCardNo = bankCardNo.substring(bankCardNo.length() - 4);
            }

            logger.info("借款成功-银行卡尾数为:{}", firebaseMsg);
            firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
            firebaseMsg = MessageFormat.format(firebaseMsg, bankCardNo);
            logger.info("借款成功-发送的firebase推送消息为:{}", firebaseMsg);
            msg.setFirebaseMsg(firebaseMsg);
            String phoneMsg = i18nService.getViMessage("firebase.notice.loansuccess.remind.phonemsg");
            phoneMsg = StringUtil.vietnameseToEnglish(phoneMsg);
            phoneMsg = MessageFormat.format(phoneMsg, bankCardNo);
            logger.info("借款成功-发送的手机短信消息为:{}", phoneMsg);
            msg.setPhoneMsg(phoneMsg);
            sendAllNotice(msg, true);
        }
    }

    @Override
    public void sendLoanFail(XUserInfo xUserInfo) {
        if (xUserInfo == null) {
            return;
        }
        NoticeMsg msg = new NoticeMsg();
        // firebase消息推送参数
        msg.setFmcToken(xUserInfo.getFmcToken());
        // 两种消息共用参数
        msg.setUserGid(xUserInfo.getUserGid());
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
        sendAllNotice(msg, false);
    }
}
