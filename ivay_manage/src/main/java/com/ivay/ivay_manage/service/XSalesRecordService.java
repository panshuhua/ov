package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XSalesRecord;

public interface XSalesRecordService {
    Boolean add(XSalesRecord xSalesRecord);

    XSalesRecord get(Long id);

    XSalesRecord update(XSalesRecord xSalesRecord);

    PageTableResponse list(PageTableRequest request);

    int delete(Long id);

    /**
     * @Description 查询电销记录
     * @Author Ryan
     * @Param [limit, num, id]
     * @Return com.ivay.ivay_common.table.PageTableResponse
     * @Date 2019/7/23 17:52
     */
    PageTableResponse getSalesRecordList(int limit, int num, int id);
}
