package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionTaskService;
import com.ivay.ivay_repository.dao.master.XCollectionTaskDao;
import com.ivay.ivay_repository.model.XCollectionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XCollectionTaskServiceImpl implements XCollectionTaskService {
    private static final Logger logger = LoggerFactory.getLogger(XCollectionTaskService.class);

    @Autowired
    private XCollectionTaskDao xCollectionTaskDao;

    @Override
    public XCollectionTask save(XCollectionTask xCollectionTask) {
        return xCollectionTaskDao.save(xCollectionTask) == 1 ? xCollectionTask : null;
    }

    @Override
    public XCollectionTask get(Long id) {
        return xCollectionTaskDao.getById(id);
    }

    @Override
    public XCollectionTask update(XCollectionTask xCollectionTask) {
        return xCollectionTaskDao.update(xCollectionTask) == 1 ? xCollectionTask : null;
    }

    @Override
    public PageTableResponse list(PageTableRequest request) {
        return new PageTableHandler(
                a -> xCollectionTaskDao.count(a.getParams()),
                a -> xCollectionTaskDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public int delete(Long id) {
        return xCollectionTaskDao.delete(id);
    }
}
