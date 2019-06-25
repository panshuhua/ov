package com.ivay.ivay_app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.tempuri.ApiBulkReturn;

import com.ivay.ivay_app.dto.SMSResponseStatus;
import com.ivay.ivay_app.service.XConfigService;
import com.ivay.ivay_app.service.XFirebaseNoticeService;
import com.ivay.ivay_app.service.XRegisterService;
import com.ivay.ivay_app.utils.FirebaseUtil;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.utils.JsonUtils;
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
                sendPhoneNotice(mobile, i18nService.getMessage("firebase.notice.audit.msg"));
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
                sendPhoneNotice(mobile, i18nService.getMessage("firebase.notice.loan.msg"));
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

    @Override
    public boolean sendRepaymentNotice() {
        // 发送到期前一天/当天提醒
        List<XOverDueFee> xOverDueFeeList = xRecordLoanDao.findOneOverdue();
        logger.info("到期前一天/当天提醒的总条数：" + xOverDueFeeList.size() + "-----------------------");

        for (XOverDueFee fee : xOverDueFeeList) {
            // 逾期一天的费用计算
        }

        List<String> registrationTokens = new ArrayList<String>();

        int i = 0;
        for (XOverDueFee fee : xOverDueFeeList) {
            try {
                String fmcToken = fee.getFmcToken();
                if (!StringUtils.isEmpty(fmcToken)) {
                    // FirebaseUtil.sendMsgToFmcToken(fmcToken,
                    // i18nService.getMessage("firebase.notice.repayment.msg"));
                    registrationTokens.add(fmcToken);
                }
                i++;
                logger.info("正在发送第" + i + "条短信----------------");
                // 发送手机短信
                String phone = fee.getPhone();
                sendPhoneNotice(phone, i18nService.getMessage("firebase.notice.repayment.msg"));
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return false;
            }
        }

        logger.info("短信已发送完毕，总共成功发送" + i + "条");

        logger.info("批量发送firebase还款通知开始-------------");
        if (registrationTokens.size() > 0) {
            try {
                FirebaseUtil.sendBatchMsgToFmcToken(registrationTokens,
                    i18nService.getMessage("firebase.notice.repayment.titlemsg"),
                    i18nService.getMessage("firebase.notice.repayment.msg"));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return false;
            }
        }
        logger.info("批量发送firebase还款通知结束-------------");

        logger.info("还款到期的通知成功发送！");
        return true;
    }

    // 通过手机短信的方式发送通知
    private void sendPhoneNotice(String mobile, String msg) {
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_SEND_PHONEMSG));
        if (config == null) {
            logger.error("发送短信验证码配置获取出错");
        }

        for (Object key : config.keySet()) {
            String value = config.get(key).toString();

            // 使用方法一发送短信验证码
            if ("1".equals(value)) {
                Map<String, String> msgMap = xRegisterService.sendMsgBySMS(mobile, msg);
                String status = msgMap.get("status");
                logger.info("SMG方式发送短信验证码返回状态，返回码：{}", status);
                if (SMSResponseStatus.SUCCESS.getCode().equals(status)) {
                    String messageid = msgMap.get("messageid");
                    logger.info("发送的短信id：" + messageid);
                    logger.info("SMG成功发送的内容是：" + msg);
                }
                return;
                // 使用方法二发送短信验证码
            } else if ("2".equals(value)) {
                ApiBulkReturn re = xRegisterService.sendMsgByVMG(mobile, msg);
                String errorCode = Long.toString(re.getError_code());

                if ("0".equals(errorCode)) {
                    logger.info("VMG发送的短信是：" + msg);
                }
                return;

                // 不发送短信验证码，直接返回随机数（把msg1和msg2都修改为0即可）
            } else if ("0".equals(value)) {
                logger.info("发送的短信是：" + msg);
                return;
            }

        }

    }

}
