package com.ivay.ivay_app.job;

import com.ivay.ivay_app.service.XRecordLoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableScheduling
public class BackgroundScheduleTask {
    private static final Logger logger = LoggerFactory.getLogger(BackgroundScheduleTask.class);

    @Autowired
    private XRecordLoanService xRecordLoanService;

    @Scheduled(cron = "${timer.overdueFee}")
    private void calcOverDueFee() {
        boolean flag = false;
        int count = 0;
        String start = "开始计算逾期费用---start";
        while (!flag) {
            if (count > 0) {
                start = "正在进行第" + count + "次重试--start";
            }
            logger.info(start);
            flag = xRecordLoanService.calcOverDueFee2();
            logger.info("逾期费用计算结束---{}", flag ? "成功" : "失败");
            if (!flag) {
                if ((count++ > 5)) {
                    flag = true;
                    logger.error("逾期费用计算出错，请及时查看");
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        logger.error("逾期费用计算暂停异常: {}", ex);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "${timer.transferTimeout}")
    private void transferTimeout() {
        xRecordLoanService.timeoutTransferInfo();
    }
}
