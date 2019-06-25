package com.ivay.ivay_app.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ivay.ivay_app.service.XFirebaseNoticeService;

@Component
@Configuration
@EnableScheduling
public class FirebaseScheduleTask {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseScheduleTask.class);

    @Autowired
    private XFirebaseNoticeService xFirebaseNoticeService;

    // 到期提醒
    @Scheduled(cron = "${timer.firebaseExpireNotice}")
    private void expireNotice() {
        boolean flag = false;
        int count = 0;
        String start = "开始发送---start";
        while (!flag) {
            if (count > 0) {
                start = "正在进行第" + count + "次重试--start";
            }

            logger.info(start);
            flag = xFirebaseNoticeService.sendRepaymentNotice();
            logger.info("还款到期通知消息发送结束---{}", flag ? "成功" : "失败");

            if (!flag) {
                if ((count++ > 5)) {
                    flag = true;
                    logger.error("还款到期通知发送失败");
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        logger.error("发送消息暂停异常: {}", ex);
                    }
                }
            }

        }

    }

    // 逾期提醒：每3天发一次
    @Scheduled(cron = "${timer.firebaseOverDueNotice}")
    private void overDueNotice() {
        boolean flag = false;
        int count = 0;
        String start = "开始发送---start";
        while (!flag) {
            if (count > 0) {
                start = "正在进行第" + count + "次重试--start";
            }

            logger.info(start);
            flag = xFirebaseNoticeService.sendOverDueNotice();
            logger.info("逾期通知消息发送结束---{}", flag ? "成功" : "失败");

            if (!flag) {
                if ((count++ > 5)) {
                    flag = true;
                    logger.error("逾期通知发送失败");
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        logger.error("发送消息暂停异常: {}", ex);
                    }
                }
            }

        }

    }

}
