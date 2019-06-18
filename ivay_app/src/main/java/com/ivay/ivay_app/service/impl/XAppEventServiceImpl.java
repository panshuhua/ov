package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_app.service.XAppEventService;
import com.ivay.ivay_repository.dao.master.XAppEventDao;
import com.ivay.ivay_repository.model.XAppEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XAppEventServiceImpl implements XAppEventService {
    private static final Logger logger = LoggerFactory.getLogger(XAppEventService.class);

    @Autowired
    private XAppEventDao xAppEventDao;

    @Override
    public XAppEvent save(XAppEvent xAppEvent) {
        return xAppEventDao.save(xAppEvent) == 1 ? xAppEvent : null;
    }

    @Override
    public XAppEvent get(Long id) {
        return xAppEventDao.getById(id);
    }

    @Override
    public XAppEvent update(XAppEvent xAppEvent) {
        return xAppEventDao.update(xAppEvent) == 1 ? xAppEvent : null;
    }

    @Override
    public PageTableResponse list(PageTableRequest request) {
        return new PageTableHandler(
                a -> xAppEventDao.count(a.getParams()),
                a -> xAppEventDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public int delete(Long id) {
        return xAppEventDao.delete(id);
    }
}
