package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_manage.service.XCollectionCalculateService;
import com.ivay.ivay_repository.dao.master.XCollectionCalculateDao;
import com.ivay.ivay_repository.dto.CollectionCalculateInfo;
import com.ivay.ivay_repository.dto.CollectionCalculateResult;
import com.ivay.ivay_repository.model.XCollectionCalculate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public void saveCollectionCalculate(Date date) {
        logger.info("开始统计昨天催收报表定时任务{}",new Date());

        // 默认统计昨天的数据
        if (null == date) {
            date = DateUtils.getBeginDayOfYesterday();
        }

        Date beginDate = DateUtils.getDayStartTime(date);
        Date endDate = DateUtils.getDayEndTime(date);

        // 查询日期，逾期账单，逾期用户，逾期本金，应收总额
        XCollectionCalculate collectonCalculate = xCollectionCalculateDao.selectCollectionsCalculate(beginDate, endDate);
        // 查询还款和人数的统计
        XCollectionCalculate repaytionCalculate = xCollectionCalculateDao.selectRepaytionCalculate(beginDate, endDate);

        collectonCalculate.setCreateTime(new Date());
        collectonCalculate.setNumberRepay(repaytionCalculate.getNumberRepay());
        collectonCalculate.setAmountRepay(repaytionCalculate.getAmountRepay());

        xCollectionCalculateDao.save(collectonCalculate);

        logger.info("昨天催收报表定时任务执行完成");
    }

    @Override
    public PageTableResponse selectCalculateList(int limit, int num, CollectionCalculateInfo collectionCalculateInfo) {

        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);
        Map param = request.getParams();

        if (null != collectionCalculateInfo) {
            param.put("beginTime", collectionCalculateInfo.getBeginTime());
            param.put("endTime", collectionCalculateInfo.getEndTime());
        }

        List<XCollectionCalculate> xCollectionCalculateList = xCollectionCalculateDao.selectCalculateList(request.getParams(), request.getOffset(), request.getLimit());
        CollectionCalculateResult result = xCollectionCalculateDao.selectTotalCalculate(request.getParams());

        if (null != result) {
            result.setXCollectionCalculateList(xCollectionCalculateList);
        }

        List<CollectionCalculateResult> resultList = new ArrayList<>();
        resultList.add(result);
        return new PageTableHandler(
                a -> xCollectionCalculateDao.selectCalculateListCount(a.getParams()),
                a -> resultList
        ).handle(request);
    }
}
