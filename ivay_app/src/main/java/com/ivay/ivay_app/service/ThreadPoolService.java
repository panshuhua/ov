package com.ivay.ivay_app.service;

public interface ThreadPoolService {
    void execute(Runnable runnable);

    void destory();
}
