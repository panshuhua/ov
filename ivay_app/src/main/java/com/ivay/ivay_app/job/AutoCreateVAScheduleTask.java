package com.ivay.ivay_app.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ivay.ivay_app.service.XVirtualAccountService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.RedisLock;

/**
 * 自动创建还款虚拟账号 一个小时创建一次
 * 
 * @author panshuhua
 * @date 2019/06/24
 */
@Component
@Configuration
public class AutoCreateVAScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(AutoCreateVAScheduleTask.class);

    @Autowired
    private RedisLock redisLock;

    @Autowired
    XVirtualAccountService xVirtualAccountService;

    @Scheduled(cron = "${timer.autoCreateVA}")
    private void autoCreateVA() {
        String date = DateUtils.getNowDateYYYYMMDD();
        if (!redisLock.tryAutoCreateVALock(date)) {
            logger.error(date + ":已经开始自动创建虚拟账号");
            return;
        }
        boolean flag = false;
        int count = 0;
        String start = "开始创建虚拟账号---start";
        while (!flag) {
            if (count > 0) {
                start = "正在进行第" + count + "次重试--start";
            }

            logger.info(start);
            flag = xVirtualAccountService.saveVirtualAccount();
            logger.info("自动创建虚拟账号结束---{}", flag ? "成功" : "失败");

            if (!flag) {
                if ((count++ > 5)) {
                    flag = true;
                    logger.error("自动创建虚拟账号出错，请及时查看");
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        logger.error("自动创建虚拟账号暂停异常: {}", ex);
                    }
                }
            }

        }
    }

}
