package com.ivay.ivay_app.job;

import com.ivay.ivay_app.service.XRecordLoanService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Configuration
@EnableScheduling
public class BackgroundScheduleTask {
    private static final Logger logger = LoggerFactory.getLogger(BackgroundScheduleTask.class);

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private XRecordLoanService xRecordLoanService;

    @Scheduled(cron = "${timer.overdueFee}")
    private void calcOverDueFee() {
        String date = DateUtils.getNowDateYYYYMMDD();
        if (!redisLock.tryOverdueFeeLock(date)) {
            logger.error(date + ":已经开始计算逾期利息");
            return;
        }
        boolean flag = false;
        int count = 0;
        String start = "开始计算逾期费用---start";
        while (!flag) {
            if (count > 0) {
                start = "正在进行第" + count + "次重试--start";
            }
            logger.info(start);
            flag = xRecordLoanService.calcOverDueFee();
            logger.info("逾期费用计算结束---{}", flag ? "成功" : "失败");
            if (!flag) {
                if ((count++ > 5)) {
                    flag = true;
                    logger.error("逾期费用计算出错，请及时查看: " + new Date());
                } else {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception ex) {
                        logger.error("逾期费用计算暂停异常: {}", ex.getMessage());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(DateUtils.getNowDateYYYYMMDD());
    }

    @Scheduled(cron = "${timer.transferTimeout}")
    private void transferTimeout() {
        xRecordLoanService.timeoutTransferInfo();
    }
}
