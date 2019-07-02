package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XCollectionTask;

public interface XCollectionTaskService {
    XCollectionTask save(XCollectionTask xCollectionTask);

    XCollectionTask get(Long id);

    XCollectionTask update(XCollectionTask xCollectionTask);

    PageTableResponse list(PageTableRequest request);

    int delete(Long id);
}
