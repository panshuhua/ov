package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.dto.CollectionCalculateInfo;
import com.ivay.ivay_repository.model.XCollectionCalculate;

import java.util.Date;
import java.util.List;

public interface XCollectionCalculateService {
    XCollectionCalculate save(XCollectionCalculate xCollectionCalculate);

    XCollectionCalculate get(Integer id);

    XCollectionCalculate update(XCollectionCalculate xCollectionCalculate);

    PageTableResponse list(PageTableRequest request);

    int delete(Integer id);

    /**
     * @Description 催收报表定时任务
     * @Author Ryan
     * @Param [date]
     * @Return void
     * @Date 2019/7/16 14:04
     */
    void saveCollectionCalculate(Date date);

    /**
     * @Description 催收报表统计列表
     * @Author Ryan
     * @Param [limit, num, collectionCalculateInfo]
     * @Return com.ivay.ivay_common.table.PageTableResponse
     * @Date 2019/7/17 14:56
     */
    PageTableResponse selectCalculateList(int limit, int num, CollectionCalculateInfo collectionCalculateInfo);

    /**
     * @Description 还款名单
     * @Author Ryan
     * @Param [limit, num, id]
     * @Return com.ivay.ivay_common.table.PageTableResponse
     * @Date 2019/7/17 18:59
     */
    PageTableResponse selectRepayList(int limit, int num, int id);

    /**
     * @Description 催收报表EXCEL导出数据
     * @Author Ryan
     * @Param [collectionCalculateInfo]
     * @Return java.util.List<com.ivay.ivay_repository.model.XCollectionCalculate>
     * @Date 2019/7/19 9:13
     */
    List<XCollectionCalculate> selectExcelList(CollectionCalculateInfo collectionCalculateInfo);
}
