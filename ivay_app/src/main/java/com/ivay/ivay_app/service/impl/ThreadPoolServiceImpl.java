package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.service.ThreadPoolService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ThreadPoolServiceImpl implements ThreadPoolService {
    // 核心线程池数
    private static int CORE_POOL_SIZE = 10;
    // 最大线程池数
    private static int MAXIMUM_POOL_SIZE = 50;
    // 空闲时间
    private static long KEEP_ALIVE_TIME = 60;
    private static String THREAD_POOL_NAME_PREFIX = "x_ool_";

    private ThreadPoolExecutor executor;

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = r -> new Thread(r, THREAD_POOL_NAME_PREFIX + r.hashCode());
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5000),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        // 预启动所有核心线程
        executor.prestartAllCoreThreads();
    }

    @Override
    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    @Override
    public void destory() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
