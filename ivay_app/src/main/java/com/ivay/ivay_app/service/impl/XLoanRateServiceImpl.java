package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.service.XConfigService;
import com.ivay.ivay_app.service.XLoanRateService;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XLoanRateDao;
import com.ivay.ivay_repository.model.XLoanRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XLoanRateServiceImpl implements XLoanRateService {
    private static final Logger logger = LoggerFactory.getLogger(XLoanRateService.class);

    @Resource
    private XLoanRateDao xLoanRateDao;

    @Resource
    private XConfigService xConfigService;

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
    public int saveByBatch(List<XLoanRate> list) {
        Date now = new Date();
        for (XLoanRate xrl : list) {
            xrl.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
            xrl.setCreateTime(now);
            xrl.setUpdateTime(now);
        }
        return xLoanRateDao.saveByBatch(list);
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

    @Override
    public int acquireLoanRate(String userGid) {
        List<XLoanRate> list = xLoanRateDao.getByGid(userGid);
        if (list.size() > 0) {
            logger.info("已有借款利率，不需要重新配置");
            return list.size();
        }

        // 借款利率配置
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_LOAN_RATE));
        if (config == null) {
            logger.error("借款利率配置获取出错:{}", SysVariable.TEMPLATE_LOAN_RATE);
            return 0;
        }

        Date now = new Date();
        for (Object key : config.keySet()) {
            BigDecimal value = new BigDecimal(config.get(key).toString());
            XLoanRate xlr = new XLoanRate();
            xlr.setCreateTime(now);
            xlr.setUpdateTime(now);
            xlr.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
            xlr.setUserGid(userGid);
            xlr.setPeriod(Integer.parseInt(key.toString()));
            xlr.setInterestRate(value);
            list.add(xlr);
        }
        return xLoanRateDao.saveByBatch(list);
    }
}
