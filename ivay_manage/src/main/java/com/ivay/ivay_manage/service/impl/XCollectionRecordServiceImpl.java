package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionRecordService;
import com.ivay.ivay_repository.dao.master.XCollectionRecordDao;
import com.ivay.ivay_repository.model.XCollectionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XCollectionRecordServiceImpl implements XCollectionRecordService {
    private static final Logger logger = LoggerFactory.getLogger(XCollectionRecordService.class);

    @Autowired
    private XCollectionRecordDao xCollectionRecordDao;

    @Override
    public XCollectionRecord save(XCollectionRecord xCollectionRecord) {
        return xCollectionRecordDao.save(xCollectionRecord) == 1 ? xCollectionRecord : null;
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
}
