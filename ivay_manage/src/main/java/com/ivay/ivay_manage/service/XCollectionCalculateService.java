package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XCollectionCalculate;

public interface XCollectionCalculateService {
    XCollectionCalculate save(XCollectionCalculate xCollectionCalculate);

    XCollectionCalculate get(Long id);

    XCollectionCalculate update(XCollectionCalculate xCollectionCalculate);

    PageTableResponse list(PageTableRequest request);

    int delete(Long id);

    /**
     * @Description 催收报表定时任务
     * @Author Ryan
     * @Param []
     * @Return void
     * @Date 2019/7/15 11:35
     */
    void saveCollectionCalculateBatch();
}
