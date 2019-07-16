package com.ivay.ivay_manage.job;

import com.ivay.ivay_manage.service.XCollectionCalculateService;
import com.ivay.ivay_manage.service.XCollectionTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @ClassName CollectionCalculateScheduleTask
 * @Description 催收报表定时任务
 * @Author Ryan
 * @Date 2019/7/15 11:31
 */
@Component
@Configuration
@EnableScheduling
public class CollectionCalculateScheduleTask {
    private static final Logger logger = LoggerFactory.getLogger(OverdueOrderScheduleTask.class);

    @Autowired
    private XCollectionCalculateService collectionCalculateService;

    @Scheduled(cron = "${timer.overdueOrder}")
    private void saveCollectionCalculateBatch(Date date) {

        logger.info("开始执行催收报表数据统计 -- saveCollectionCalculateBatch");

        collectionCalculateService.saveCollectionCalculateBatch(date);

        logger.info("结束执行催收报表数据统计 -- overdueOrder");
    }
}
