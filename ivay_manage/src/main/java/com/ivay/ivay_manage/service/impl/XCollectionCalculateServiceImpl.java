package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionCalculateService;
import com.ivay.ivay_repository.dao.master.XCollectionCalculateDao;
import com.ivay.ivay_repository.model.XCollectionCalculate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class XCollectionCalculateServiceImpl implements XCollectionCalculateService {
    private static final Logger logger = LoggerFactory.getLogger(XCollectionCalculateService.class);

    @Autowired
    private XCollectionCalculateDao xCollectionCalculateDao;

    @Override
    public XCollectionCalculate save(XCollectionCalculate xCollectionCalculate) {
        return xCollectionCalculateDao.save(xCollectionCalculate) == 1 ? xCollectionCalculate : null;
    }

    @Override
    public XCollectionCalculate get(Long id) {
        return xCollectionCalculateDao.getById(id);
    }

    @Override
    public XCollectionCalculate update(XCollectionCalculate xCollectionCalculate) {
        return xCollectionCalculateDao.update(xCollectionCalculate) == 1 ? xCollectionCalculate : null;
    }

    @Override
    public PageTableResponse list(PageTableRequest request) {
        return new PageTableHandler(
                a -> xCollectionCalculateDao.count(a.getParams()),
                a -> xCollectionCalculateDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public int delete(Long id) {
        return xCollectionCalculateDao.delete(id);
    }

    @Override
    public void saveCollectionCalculateBatch() {
        logger.info("开始统计催收报表定时任务{}",new Date());

        //查询日期，逾期账单，逾期用户，逾期本金，应收总额

    }
}
