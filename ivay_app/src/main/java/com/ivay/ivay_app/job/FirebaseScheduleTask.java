package com.ivay.ivay_app.job;

import com.ivay.ivay_app.service.XFirebaseNoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
//@EnableScheduling
public class FirebaseScheduleTask {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseScheduleTask.class);

    @Autowired
    private XFirebaseNoticeService xFirebaseNoticeService;

    //@Scheduled(cron = "${timer.firebaseNotice}")
    private void firebaseNotice() {
        boolean flag = false;
        int count = 0;
        String start = "开始发送---start";
        while (!flag) {
            if (count > 0) {
                start = "正在进行第" + count + "次重试--start";
            }

            logger.info(start);

//            boolean auditFlag=xFirebaseNoticeService.sendAuditNotice();
//            boolean loanFlag=xFirebaseNoticeService.sendLoanNotice();

            flag = xFirebaseNoticeService.sendRepaymentNotice();

//            if(auditFlag && loanFlag && repaymentFlag) {
//            	 logger.info("消息发送结束---{}", flag ? "成功" : "失败");
//            }

            logger.info("消息发送结束---{}", flag ? "成功" : "失败");

            if (!flag) {
//                if (!auditFlag) {
//                    flag = true;
//                    logger.error("审核通知发送失败");
//                } else if(!loanFlag) {
//                	flag = true;
//                    logger.error("放款通知发送失败");
//                } else if(!repaymentFlag) {
                //if(!repaymentFlag) {
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
}
