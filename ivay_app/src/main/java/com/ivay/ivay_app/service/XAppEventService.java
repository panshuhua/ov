package com.ivay.ivay_app.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XAppEvent;

public interface XAppEventService {
    XAppEvent save(XAppEvent xAppEvent);

    XAppEvent get(Long id);

    XAppEvent update(XAppEvent xAppEvent);

    PageTableResponse list(PageTableRequest request);

    int delete(Long id);
}
