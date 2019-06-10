package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.service.RiskUserService;
import com.ivay.ivay_manage.service.ThreadPoolService;
import com.ivay.ivay_manage.service.XConfigService;
import com.ivay.ivay_manage.service.XLoanRateService;
import com.ivay.ivay_repository.dao.master.XLoanRateDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.RiskUser;
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
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Resource
    private XLoanRateDao xLoanRateDao;

    @Resource
    private XConfigService xConfigService;

    @Resource
    private XUserInfoDao xUserInfoDao;

    @Resource
    private XRecordLoanDao xRecordLoanDao;

    @Autowired
    private ThreadPoolService threadPoolService;

    @Autowired
    private RiskUserService riskUserService;

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
        // 授信額度
        threadPoolService.execute(() -> {
            logger.info("开始计算授信额度");
            acquireCreditLimit(userGid);
        });
        // 借款利率
        threadPoolService.execute(() -> {
            logger.info("开始计算借款利率");
            acquireLoanRate(userGid);
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
            throw new BusinessException("0014", "用户不存在");
        }
        if (checkCreditLimit(xUserInfo)) {
            logger.info("进行提额..");
            updateCreditLimit(xUserInfo);
        }
        return xUserInfo.getCreditLine();
    }

    /**
     * 判断是否进行提额或初始化授信额度
     *
     * @param xUserInfo
     * @return
     */
    private boolean checkCreditLimit(XUserInfo xUserInfo) {
        if (xUserInfo.getCreditLine() == null || xUserInfo.getCreditLine() == 0) {
            // 未初始化授信额度，返回true
            return true;
        }

        // 判断提额权限
        Map<String, Object> params = new HashMap<>();
        params.put("userGid", xUserInfo.getUserGid());
        List<XRecordLoan> list = xRecordLoanDao.list(params, 1, 0);
        // 已初始化授信额度，但没借过钱
        if (list.size() == 0) {
            return false;
        }

        // 计算最后一笔结清订单的逾期时间
        long overdueDay = 0;
        Date lastRepaymentDay = null;
        for (XRecordLoan xrl : list) {
            // 已还清的借款
            if (SysVariable.REPAYMENT_STATUS_SUCCESS == xrl.getRepaymentStatus()) {
                // 最近一笔还款
                if (lastRepaymentDay == null || DateUtils.isDateAfter(lastRepaymentDay, xrl.getLastRepaymentTime()) < 0) {
                    lastRepaymentDay = xrl.getLastRepaymentTime();
                    // 逾期
                    if (DateUtils.isDateAfter(lastRepaymentDay, xrl.getDueTime()) > 0) {
                        overdueDay = Long.parseLong(DateUtils.getTwoDay(xrl.getDueTime(), lastRepaymentDay));
                    }
                }
            }
        }
        // 最近一笔结清订单的逾期天数>5不提额
        if (overdueDay > 5) {
            return false;
        }

        // 與第一次交易的天數間隔
        Date firstRepaymentTime = xRecordLoanDao.getFirstRepaymentTime(xUserInfo.getUserGid());
        if (firstRepaymentTime == null) {
            return false;
        }
        long diff = Long.parseLong(DateUtils.getTwoDay(firstRepaymentTime, new Date()));
        int count = xUserInfo.getCreditLineCount() == null ? 0 : xUserInfo.getCreditLineCount();
        // 根据实际还清借款时间触发, 第N次提额时间距离首笔交易时间分别不小于5*N天
        return 5 * (count + 1) <= diff;
    }

    private void updateCreditLimit(XUserInfo xUserInfo) {
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_CREDIT_LIMIT));
        if (config == null) {
            logger.error("提額配置获取出错");
        } else {
            String typeFlag = "normal";
            long borrowAmount = 0;
            // 判斷 用户 是白名單用戶還是正常用戶
            List<RiskUser> whiteList = riskUserService.selectUserListByPhone(xUserInfo.getPhone());
            if (whiteList.size() > 0) {
                typeFlag = "white";
                borrowAmount = Long.parseLong(whiteList.get(0).getAmount());
            }
            Map creditConfig = (LinkedHashMap) config.get(typeFlag);

            if (xUserInfo.getCreditLine() == null || xUserInfo.getCreditLine() == 0) {
                // 设置授信额度
                if (borrowAmount == 0) {
                    borrowAmount = Long.parseLong(creditConfig.get("start").toString());
                }
                xUserInfo.setCreditLine(borrowAmount);
                xUserInfo.setCanborrowAmount(xUserInfo.getCreditLine());
                xUserInfo.setCreditLineCount(0);
                xUserInfoDao.update(xUserInfo);
            } else {
                // 提额
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
