package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.advice.BusinessException;
import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.service.ThreadPoolService;
import com.ivay.ivay_app.service.XConfigService;
import com.ivay.ivay_app.service.XLoanRateService;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XLoanRateDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.XLoanRate;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class XLoanRateServiceImpl implements XLoanRateService {
    private static final Logger logger = LoggerFactory.getLogger(XLoanRateServiceImpl.class);

    @Resource
    private XLoanRateDao xLoanRateDao;

    @Resource
    private XConfigService xConfigService;

    @Resource
    private XUserInfoDao xUserInfoDao;

    @Resource
    private I18nService i18nService;

    @Resource
    private XRecordLoanDao xRecordLoanDao;

    @Autowired
    private ThreadPoolService threadPoolService;

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
        return new PageTableHandler((a) -> xLoanRateDao.count(a.getParams()),
                (a) -> xLoanRateDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public void initLoanRateAndCreditLimit(String userGid) {
        // 借款利率
        threadPoolService.execute(() -> {
            acquireLoanRate(userGid);
        });
        // 授信額度
        threadPoolService.execute(() -> {
            acquireCreditLimit(userGid);
        });
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
            logger.error("借款利率配置获取出错");
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

    @Override
    public long acquireCreditLimit(String userGid) {
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        if (xUserInfo == null) {
            throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
        }
        if (checkCreditLimit(xUserInfo)) {
            updateCreditLimit(xUserInfo);
        }
        return xUserInfo.getCreditLine();
    }

    private boolean checkCreditLimit(XUserInfo xUserInfo) {
        if (xUserInfo.getCreditLine() == null || xUserInfo.getCreditLine() == 0) {
            return true;
        }
        // 提額權限判斷
        Map<String, Object> params = new HashMap<>();
        params.put("userGid", xUserInfo.getUserGid());
        List<XRecordLoan> list = xRecordLoanDao.list(params, 1, 0);
        // 沒接過錢，即初始化，給提額
        if (list.size() == 0) {
            return true;
        }
        long overdueDay = 0;
        Date lastRepaymentDay = null;
        for (XRecordLoan xrl : list) {
            if (SysVariable.REPAYMENT_STATUS_SUCCESS == xrl.getRepaymentStatus()) {
                // 最近一筆還款
                if (lastRepaymentDay == null || DateUtils.isDateAfter(lastRepaymentDay, xrl.getLastRepaymentTime()) < 0) {
                    lastRepaymentDay = xrl.getLastRepaymentTime();
                    // 逾期
                    if (DateUtils.isDateAfter(lastRepaymentDay, xrl.getDueTime()) > 0) {
                        overdueDay = Long.parseLong(DateUtils.getTwoDay(xrl.getDueTime(), lastRepaymentDay));
                    }
                }
            }
        }

        if (overdueDay > 5) {
            return false;
        }
        Date firstRepaymentTime = xRecordLoanDao.getFirstRepaymentTime(xUserInfo.getUserGid());
        // 與第一次交易的天數間隔
        long diff = Long.parseLong(DateUtils.getTwoDay(firstRepaymentTime, new Date()));
        int count = xUserInfo.getCreditLineCount() == null ? 0 : xUserInfo.getCreditLineCount();
        return 5 * (count + 1) <= diff;
    }

    private void updateCreditLimit(XUserInfo xUserInfo) {
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_CREDIT_LIMIT));
        if (config == null) {
            logger.error("提額配置获取出错");
        } else {
            String flag = "normal"; // 白名單 white
            Map creditConfig = (LinkedHashMap) config.get(flag);

            if (xUserInfo.getCreditLine() == null || xUserInfo.getCreditLine() == 0) {
                xUserInfo.setCreditLine(Long.parseLong(creditConfig.get("start").toString()));
                xUserInfo.setCanborrowAmount(xUserInfo.getCreditLine());
                xUserInfo.setCreditLineCount(0);
                xUserInfoDao.update(xUserInfo);
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put("userGid", xUserInfo.getUserGid());
                params.put("repaymentStatus", SysVariable.REPAYMENT_STATUS_SUCCESS);
                int count = xRecordLoanDao.count(params);
                if (count > 0) {
                    Object step = ((LinkedHashMap) creditConfig.get("step")).get(String.valueOf(count));
                    if (step == null) {
                        step = ((LinkedHashMap) creditConfig.get("step")).get(">");
                    }
                    long s = Long.parseLong(step.toString());
                    long max = Long.parseLong(creditConfig.get("end").toString());
                    if (max < s + xUserInfo.getCreditLine()) {
                        s = max - xUserInfo.getCreditLine();
                    }
                    xUserInfo.setCreditLine(s + xUserInfo.getCreditLine());
                    xUserInfo.setCanborrowAmount(s + xUserInfo.getCanborrowAmount());
                    int n = xUserInfo.getCreditLineCount() == null ? 0 : xUserInfo.getCreditLineCount();
                    xUserInfo.setCreditLineCount(n + 1);
                    logger.info("第{}次提額，提額數目: {}, 新額度: {}", xUserInfo.getCreditLineCount(), s, xUserInfo.getCreditLine());
                    xUserInfoDao.update(xUserInfo);
                }
            }
        }
    }
}
