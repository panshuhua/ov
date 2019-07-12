package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.dto.CollectionTaskInfo;
import com.ivay.ivay_repository.model.XCollectionTask;

public interface XCollectionTaskService {
    XCollectionTask save(XCollectionTask xCollectionTask);

    XCollectionTask get(Integer id);

    XCollectionTask update(XCollectionTask xCollectionTask);

    PageTableResponse list(int limit, int num, CollectionTaskInfo collectionTaskInfo);

    int delete(Long id);

    /**
     * @Description 执行扫描过期订单，生成催收档案
     * @Author Ryan
     * @Param []
     * @Return void
     * @Date 2019/7/9 10:48
     */
    void saveCollectionTaskBatch();

    /**
     * @Description 指派催收人
     * @Author Ryan
     * @Param [collectorId, id]
     * @Return int
     * @Date 2019/7/10 11:44
     */
    boolean updateCollector(int collectorId, int id);

    /**
     * @Description 我的催收
     * @Author Ryan
     * @Param [limit, num, collectionTaskInfo]
     * @Return com.ivay.ivay_common.table.PageTableResponse
     * @Date 2019/7/12 9:19
     */
    PageTableResponse getCollectionListByUserGid(int limit, int num, CollectionTaskInfo collectionTaskInfo);

    /**
     * @Description 催收回款列表
     * @Author Ryan
     * @Param [limit, num, collectionTaskInfo]
     * @Return com.ivay.ivay_common.table.PageTableResponse
     * @Date 2019/7/12 15:57
     */
    PageTableResponse getCollectionsRepayList(int limit, int num, CollectionTaskInfo collectionTaskInfo);
}
