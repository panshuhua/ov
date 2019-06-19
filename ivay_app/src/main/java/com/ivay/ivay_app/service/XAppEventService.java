package com.ivay.ivay_app.service;

import com.ivay.ivay_repository.model.XAppEvent;

public interface XAppEventService {
    XAppEvent save(XAppEvent xAppEvent);

    XAppEvent get(String gid);

    int delete(String gid);
}
