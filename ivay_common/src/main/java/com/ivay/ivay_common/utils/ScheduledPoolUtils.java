package com.ivay.ivay_common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ScheduledPoolUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledPoolUtils.class);
    private static ScheduledExecutorService scheduledExecutorService;

    public static ScheduledExecutorService getInstance() {
        if (scheduledExecutorService == null) {
            int corePoolSize = 5;
            ThreadFactory threadFactory = r -> new Thread(r, "time_pool_" + r.hashCode());
            RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();
            scheduledExecutorService = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, handler);
            logger.info("定时线程池初始化...");
        }
        return scheduledExecutorService;
    }
}
