package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XCollectionRecord;

public interface XCollectionRecordService {
    XCollectionRecord save(XCollectionRecord xCollectionRecord);

    XCollectionRecord get(Long id);

    XCollectionRecord update(XCollectionRecord xCollectionRecord);

    PageTableResponse list(PageTableRequest request);

    int delete(Long id);
}
