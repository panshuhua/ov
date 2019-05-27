package com.ivay.ivay_manage.service;

public interface ThreadPoolService {
    void execute(Runnable runnable);

    void destory();
}
