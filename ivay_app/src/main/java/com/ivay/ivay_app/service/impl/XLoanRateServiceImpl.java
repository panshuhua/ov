package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.service.XLoanRateService;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XLoanRateDao;
import com.ivay.ivay_repository.model.XLoanRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class XLoanRateServiceImpl implements XLoanRateService {
    private static final Logger logger = LoggerFactory.getLogger(XLoanRateService.class);

    @Resource
    private XLoanRateDao xLoanRateDao;

    @Override
    public int save(XLoanRate xLoanRate) {
        XLoanRate old = xLoanRateDao.getByUserAndPeriod(xLoanRate.getUserGid(), xLoanRate.getPeriod());
        Date now = new Date();
        xLoanRate.setUpdateTime(now);
        xLoanRate.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
        if (old == null) {
            xLoanRate.setCreateTime(now);
            return xLoanRateDao.save(xLoanRate);
        } else {
            xLoanRate.setId(old.getId());
            xLoanRate.setCreateTime(old.getCreateTime());
            return xLoanRateDao.update(xLoanRate);
        }
    }

    @Override
    public PageTableResponse list(int limit, int num, String userGid) {
        PageTableRequest request = new PageTableRequest();
        request.setOffset(limit * (num - 1));
        request.setLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "period");
        params.put("userGid", userGid);
        request.setParams(params);
        return new PageTableHandler(a -> xLoanRateDao.count(a.getParams()),
                a -> xLoanRateDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }
}
