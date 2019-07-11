package com.ivay.ivay_manage.job;

import com.ivay.ivay_manage.service.XCollectionTaskService;
import com.ivay.ivay_manage.service.XLoanService;
import com.ivay.ivay_repository.model.XCollectionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ClassName OverdueOrderScheduleTask
 * @Description 扫描过期订单，生成催收档案
 * @Author Ryan
 * @Date 2019/7/9 10:25
 */
@Component
@Configuration
@EnableScheduling
public class OverdueOrderScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(OverdueOrderScheduleTask.class);

    @Autowired
    private XCollectionTaskService collectionTaskService;

    @Scheduled(cron = "${timer.overdueOrder}")
    private void overdueOrder() {

        logger.info("开始扫描过期订单，生成催收档案 -- overdueOrder");

        collectionTaskService.saveCollectionTaskBatch();

        logger.info("完成扫描过期订单，生成催收档案 -- overdueOrder");
    }
}
