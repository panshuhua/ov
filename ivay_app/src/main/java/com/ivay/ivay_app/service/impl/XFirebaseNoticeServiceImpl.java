package com.ivay.ivay_app.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.tempuri.ApiBulkReturn;

import com.ivay.ivay_app.dto.MsgLinkData;
import com.ivay.ivay_app.dto.NoticeMsg;
import com.ivay.ivay_app.dto.SMSResponseStatus;
import com.ivay.ivay_app.service.XConfigService;
import com.ivay.ivay_app.service.XFirebaseNoticeService;
import com.ivay.ivay_app.service.XRecordLoanService;
import com.ivay.ivay_app.service.XRegisterService;
import com.ivay.ivay_app.utils.FirebaseUtil;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dto.XOverDueFee;
import com.ivay.ivay_repository.model.XUserInfo;

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
    XRegisterService xRegisterService;
    @Autowired
    XRecordLoanDao xRecordLoanDao;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("${noticemsg.effectiveTime}")
    private long effectiveTime;

    @Override
    public boolean sendAuditNotice() {
        List<XUserInfo> xUserInfos = xUserInfoDao.findAuditPassUsers();
        List<String> registrationTokens = new ArrayList<String>();
        for (XUserInfo userInfo : xUserInfos) {
            try {
                String fmcToken = userInfo.getFmcToken();

                if (!StringUtils.isEmpty(fmcToken)) {
                    // 单个发
                    // FirebaseUtil.sendMsgToFmcToken(fmcToken, i18nService.getMessage("firebase.notice.audit.msg"));
                    registrationTokens.add(fmcToken);
                }
                // 发送手机短信
                String mobile = userInfo.getPhone();
                // sendPhoneNotice(mobile, i18nService.getMessage("firebase.notice.audit.msg"), null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return false;
            }
        }

        // 批量发送firebase推送消息
        if (registrationTokens.size() > 0) {
            try {
                FirebaseUtil.sendBatchMsgToFmcToken(registrationTokens,
                    i18nService.getMessage("firebase.notice.audit.titlemsg"),
                    i18nService.getMessage("firebase.notice.audit.msg"));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return false;
            }
        }

        logger.info("审核通知成功发送！");
        return true;

    }

    @Override
    public boolean sendLoanNotice() {
        List<XUserInfo> xUserInfos = xUserInfoDao.findLoanSuccessUsers();
        List<String> registrationTokens = new ArrayList<String>();
        for (XUserInfo userInfo : xUserInfos) {
            try {
                String fmcToken = userInfo.getFmcToken();
                if (!StringUtils.isEmpty(fmcToken)) {
                    // FirebaseUtil.sendMsgToFmcToken(fmcToken, i18nService.getMessage("firebase.notice.loan.msg"));
                    registrationTokens.add(fmcToken);
                }

                // 发送手机短信
                String mobile = userInfo.getPhone();
                // sendPhoneNotice(mobile, i18nService.getMessage("firebase.notice.loan.msg"), null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return false;
            }

        }

        if (registrationTokens.size() > 0) {
            try {
                FirebaseUtil.sendBatchMsgToFmcToken(registrationTokens,
                    i18nService.getMessage("firebase.notice.loan.titlemsg"),
                    i18nService.getMessage("firebase.notice.loan.msg"));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return false;
            }
        }

        logger.info("借款成功的通知成功发送！");
        return true;

    }

    @Autowired
    private XRecordLoanService xRecordLoanService;

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
        // 计算逾期一天的滞纳金
        // xRecordLoanService.calc1DayOverDueFee(xOverDueFeeList);

        int i = 0;
        for (XOverDueFee fee : xOverDueFeeList) {
            try {
                i++;
                String fmcToken = fee.getFmcToken();
                // 测试
                // fmcToken =
                // "fd23FeQwuyE:APA91bHeUhfS-suh6yZifvtCY8FJVTPZ3ApMDe9tKv6ogUhFmiF7hHxhGIyyr5CA0yICKDpwscFxgk9W6XEMKSPNg_2aUZ3pwdxG5ZHz94rW-Z-qz439T60LrsxjUfEtUvNdAu73zhhQ";
                Date dueTime = fee.getDueTime();
                String dt = DateUtils.dateToString(dueTime, "dd-MM-yyyy");
                // 随机生成短链接的key
                String key = MsgAuthCode.getNumBigCharRandom(6);
                String url = SysVariable.PHONEMSG_PREFIX_LINK + key;
                logger.info("生成的key为：" + key + "------------------------");
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
                // 测试
                // String gid = "3609752e4ded4abc80a3912b7b8181a9";
                NoticeMsg msg = new NoticeMsg();
                msg.setPageId(SysVariable.PAGE_BILLDETAIL);
                msg.setGid(gid);
                if (!StringUtils.isEmpty(fmcToken)) {
                    try {
                        msg.setFmcToken(fmcToken);
                        msg.setFirebaseMsg(message);
                        msg.setTitle(title);
                        FirebaseUtil.sendMsgToFmcToken(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        firebaseFlag = false;
                    }
                }

                logger.info("发送firebase到期通知结束-------------");

                boolean phoneFlag = true;
                // 发送手机短信
                String phone = fee.getPhone();
                // 测试
                // phone = "0906716668";
                String userGid = fee.getUserGid();
                // String userGid="fd22ad29a344472eb3f35304a218bf01";
                try {
                    msg.setUserGid(userGid);
                    msg.setPhone(phone);
                    msg.setPhoneMsg(phoneMsg);
                    msg.setKey(key);
                    phoneFlag = sendPhoneNotice(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    phoneFlag = false;
                }
                // 测试发1条
                // if (i == 1) {
                // break;
                // }

                // 推送和短信都出现异常
                if (!firebaseFlag && !phoneFlag) {
                    return false;
                }
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return false;
            }

        }

        logger.info("发送完毕，总共成功发送" + i + "条");

        return true;

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
            if ("1".equals(value)) {
                Map<String, String> msgMap = xRegisterService.sendMsgBySMS(msg.getPhone(), msg.getPhoneMsg());
                String status = msgMap.get("status");
                logger.info("SMG方式发送短信验证码返回状态，返回码：{}", status);
                if (SMSResponseStatus.SUCCESS.getCode().equals(status)) {
                    String messageid = msgMap.get("messageid");
                    logger.info("发送的短信id：" + messageid);
                    logger.info("SMG成功发送的内容是：" + msg);
                    // 把需要跳转存储的数据放入redis中
                    redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                    return true;
                }

                // 使用方法二发送短信验证码
            } else if ("2".equals(value)) {
                ApiBulkReturn re = xRegisterService.sendMsgByVMG(msg.getPhone(), msg.getPhoneMsg());
                String errorCode = Long.toString(re.getError_code());

                if ("0".equals(errorCode)) {
                    logger.info("VMG发送的短信是：" + msg);
                    redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
                    return true;
                }

            } else if ("3".equals(value)) {
                String responseBody = "";
                Map<String, String> map = null;
                try {
                    responseBody = xRegisterService.sendMsgByFpt(msg.getPhone(), msg.getPhoneMsg());
                    map = JsonUtils.jsonToMap(responseBody);
                    String messageId = map.get("MessageId");
                    logger.info("fpt方式发送的短信id：" + messageId);
                    if (messageId != null) {
                        logger.info("Fpt方式发送的短信验证码是：" + msg);
                        redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
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
            } else if ("0".equals(value)) {
                redisTemplate.opsForValue().set(msg.getKey(), dataJson, effectiveTime, TimeUnit.SECONDS);
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
                // 测试
                // fmcToken =
                // "eXjyzQAuj2M:APA91bGV-yuE6ZCt_SCtVq4Xp6EpdzT_22Xq5ctlbCdk46cOLx4I7GP5zTf9BlTLrHqI4kW-i0EZlbtTRd-enm0QUxVkOhWvIRrH9JuiJ5ZY2MQTBoa4iKn0wp-fHEopMgESpj8fb54e";
                title = i18nService.getViMessage("firebase.notice.overdue.remind.titlemsg");
                message = i18nService.getViMessage("firebase.notice.overdue.remind.msg");
                Integer dueDate = fee.getDueDate(); // 逾期天数
                long overdueFee = fee.getOverdueFee() + fee.getOverdueInterest(); // 逾期后：逾期费用+逾期利息
                message = MessageFormat.format(message, dueDate, overdueFee);
                title = StringUtil.vietnameseToEnglish(title);
                message = StringUtil.vietnameseToEnglish(message);

                logger.info("发送的第" + i + "条逾期消息为：" + message);
                logger.info("正在发送第" + i + "条信息----------------");
                logger.info("发送firebase到期通知开始-------------");

                if (!StringUtils.isEmpty(fmcToken)) {
                    String gid = fee.getGid();
                    String userGid = fee.getUserGid();
                    // 测试
                    // String gid = "513fa2f1c51448bebf886861991f9322"; // 借款gid
                    NoticeMsg msg = new NoticeMsg();
                    msg.setFmcToken(fmcToken);
                    msg.setFirebaseMsg(message);
                    msg.setTitle(title);
                    msg.setPageId(SysVariable.PAGE_BILLDETAIL);
                    msg.setGid(gid);
                    msg.setUserGid(userGid);
                    FirebaseUtil.sendMsgToFmcToken(msg);
                }
                logger.info("发送firebase到期通知结束-------------");
                // 测试发1条
                // if (i == 1) {
                // break;
                // }
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
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
        MsgLinkData data = JsonUtils.jsonToPojo(dataJson, MsgLinkData.class);
        return data;
    }

    @Override
    public String testSendMsg(NoticeMsg msg, String type) throws Exception {
        if ("1".equals(type)) {
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
        } else if ("2".equals(type)) {
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
