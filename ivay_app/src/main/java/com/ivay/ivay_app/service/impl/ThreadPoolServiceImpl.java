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
    private ThreadPoolExecutor executor;

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = r -> new Thread(r, "manage_" + r.hashCode());
        executor = new ThreadPoolExecutor(10, // 核心线程数
                50, // 最大线程池数
                60, // 空闲时间
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
