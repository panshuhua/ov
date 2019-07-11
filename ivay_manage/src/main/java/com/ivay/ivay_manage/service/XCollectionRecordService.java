package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XCollectionRecord;

public interface XCollectionRecordService {
    int save(XCollectionRecord xCollectionRecord);

    XCollectionRecord get(Long id);

    XCollectionRecord update(XCollectionRecord xCollectionRecord);

    PageTableResponse list(PageTableRequest request);

    int delete(Long id);

    /**
     * @Description 查询列表
     * @Author Ryan
     * @Param [request]
     * @Return com.ivay.ivay_common.table.PageTableResponse
     * @Date 2019/7/10 19:29
     */
    PageTableResponse selectCollectionRecordList(int limit, int num, int id);
}
