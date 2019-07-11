package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionRecordService;
import com.ivay.ivay_manage.service.XCollectionTaskService;
import com.ivay.ivay_repository.dao.master.XCollectionRecordDao;
import com.ivay.ivay_repository.model.XCollectionRecord;
import com.ivay.ivay_repository.model.XCollectionTask;
import com.ivay.ivay_repository.model.XRecordLoan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class XCollectionRecordServiceImpl implements XCollectionRecordService {
    private static final Logger logger = LoggerFactory.getLogger(XCollectionRecordService.class);

    @Autowired
    private XCollectionRecordDao xCollectionRecordDao;
    @Autowired
    private XCollectionTaskService xCollectionTaskService;

    @Override
    public int save(XCollectionRecord xCollectionRecord) {
        //查询该订单的指派人，判断是否有添加权限
        XCollectionTask xCollectionTask = xCollectionTaskService.get(xCollectionRecord.getTaskId());

        if(xCollectionTask != null && xCollectionTask.getCollectorId() == xCollectionRecord.getCollectorId()){
            xCollectionRecord.setCreateTime(new Date());
            xCollectionRecord.setUpdateTime(xCollectionRecord.getCreateTime());
            xCollectionRecord.setEnableFlag("Y");

            return xCollectionRecordDao.save(xCollectionRecord);
        }
        return 0;
    }

    @Override
    public XCollectionRecord get(Long id) {
        return xCollectionRecordDao.getById(id);
    }

    @Override
    public XCollectionRecord update(XCollectionRecord xCollectionRecord) {
        return xCollectionRecordDao.update(xCollectionRecord) == 1 ? xCollectionRecord : null;
    }

    @Override
    public PageTableResponse list(PageTableRequest request) {
        return new PageTableHandler(
                a -> xCollectionRecordDao.count(a.getParams()),
                a -> xCollectionRecordDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public int delete(Long id) {
        return xCollectionRecordDao.delete(id);
    }

    @Override
    public PageTableResponse selectCollectionRecordList(PageTableRequest request) {
        return new PageTableHandler(
                a -> xCollectionRecordDao.selectCollectionCount(a.getParams()),
                a -> xCollectionRecordDao.selectCollectionRecordList(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }
}
