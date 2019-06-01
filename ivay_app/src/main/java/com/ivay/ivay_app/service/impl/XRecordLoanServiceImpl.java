package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.advice.BusinessException;
import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.*;
import com.ivay.ivay_app.utils.RedisLock;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.CommonUtil;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_common.utils.UUIDUtils;
import com.ivay.ivay_repository.dao.master.XLoanRateDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserBankcardInfoDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.XBankAndCardInfo;
import com.ivay.ivay_repository.model.XLoanRate;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class XRecordLoanServiceImpl implements XRecordLoanService {
    private static final Logger logger = LoggerFactory.getLogger(XRecordLoanServiceImpl.class);
    @Resource
    private XRecordLoanDao xRecordLoanDao;

    @Resource
    private XUserInfoDao xUserInfoDao;

    @Autowired
    private BillCommonService billCommonService;

    @Autowired
    private XConfigService xConfigService;

    @Resource
    private XLoanRateDao xLoanRateDao;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private XAPIService xapiService;
    @Autowired
    private XUserBankcardInfoDao xUserBankcardInfoDao;

    @Autowired
    private ThreadPoolService threadPoolService;

    @Autowired
    private I18nService i18nService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${risk_control_url}")
    private String riskControlUrl;

    @Autowired
    private RedisLock redisLock;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XRecordLoan borrowMoney(XRecordLoan xRecordLoan, String password) {
        if (!redisLock.tryBorrowLock(xRecordLoan.getUserGid())) {
            throw new BusinessException(i18nService.getMessage("response.error.borrow.redisError.code"),
                    i18nService.getMessage("response.error.borrow.redisError.msg"));
        }
        try {
            // region -- 借款
            XUserInfo xUserInfo = xUserInfoDao.getByGid(xRecordLoan.getUserGid());
            if (xUserInfo == null) {
                throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                        i18nService.getMessage("response.error.user.checkgid.msg"));
            }
            // 校验交易密码
            if (!bCryptPasswordEncoder.matches(password, xUserInfo.getTransPwd())) {
                throw new BusinessException(i18nService.getMessage("response.error.borrow.tranpwd.code"),
                        i18nService.getMessage("response.error.borrow.tranpwd.msg"));
            }
            // 校验可借额度
            if (xUserInfo.getCanborrowAmount() < xRecordLoan.getLoanAmount()) {
                throw new BusinessException(i18nService.getMessage("response.error.loanAmount.more.code"),
                        i18nService.getMessage("response.error.loanAmount.more.msg"));
            }

            // 校验是否有逾期借款记录
            PageTableResponse list = borrowList(0, 1, xRecordLoan.getUserGid());
            Date now = new Date();
            if (list.getData() != null && !list.getData().isEmpty()) {
                for (Object obj : list.getData()) {
                    XRecordLoan xrl = (XRecordLoan) obj;
                    if (xrl.getLoanStatus() == SysVariable.LOAN_STATUS_SUCCESS &&
                            xrl.getRepaymentStatus() != SysVariable.REPAYMENT_STATUS_SUCCESS &&
                            now.getTime() > xrl.getDueTime().getTime()) {
                        throw new BusinessException(i18nService.getMessage("response.error.borrow.hasOverDue.code"),
                                i18nService.getMessage("response.error.borrow.hasOverDue.msg"));
                    }
                }
            }

            XBankAndCardInfo card = xUserBankcardInfoDao.getBankAndCardByGid(xRecordLoan.getBankcardGid(), xRecordLoan.getUserGid());
            // 校验银行卡
            if (card == null) {
                throw new BusinessException(i18nService.getMessage("response.error.card.lack.code"),
                        i18nService.getMessage("response.error.card.lack.msg"));
            }

            xRecordLoan.setGid(UUIDUtils.getUUID());
            // 借款单号
            xRecordLoan.setOrderId(billCommonService.getBillNo());
            // 借款手续费+利息
            XLoanRate xLoanRate = xLoanRateDao.getByUserAndPeriod(xRecordLoan.getUserGid(), xRecordLoan.getLoanPeriod());
            if (xLoanRate == null) {
                throw new BusinessException(i18nService.getMessage("response.error.loanRate.lack.code"),
                        i18nService.getMessage("response.error.loanRate.lack.msg"));
            }
            // 借款利率
            xRecordLoan.setLoanRate(xLoanRate.getInterestRate());
            // 砍頭息：日利率設置成0.1%
            xRecordLoan.setInterest(xRecordLoan.getLoanAmount() / 1000 * xRecordLoan.getLoanPeriod());
            // 借款服务费：實際借款利息-砍頭息
            xRecordLoan.setFee(CommonUtil.longMultiplyBigDecimal(xRecordLoan.getLoanAmount(), xRecordLoan.getLoanRate()) - xRecordLoan.getInterest());
            // 实际到账金额：总金额-手续费-砍頭息
            xRecordLoan.setNetAmount(xRecordLoan.getLoanAmount() - xRecordLoan.getFee() - xRecordLoan.getInterest());
            // 应还金额
            xRecordLoan.setDueAmount(xRecordLoan.getLoanAmount());
            // 应还逾期滞纳金
            xRecordLoan.setOverdueFee(0L);
            // 逾期总滞纳金
            xRecordLoan.setOverdueFeeTotal(0L);
            // 应还逾期计息
            xRecordLoan.setOverdueInterest(0L);
            // 总计息
            xRecordLoan.setOverdueInterestTotal(xRecordLoan.getInterest());
            // 借款状态
            xRecordLoan.setLoanStatus(SysVariable.LOAN_STATUS_WAITING);
            // 还款状态
            xRecordLoan.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_NONE);
            xRecordLoan.setCreateTime(now);
            xRecordLoan.setUpdateTime(now);
            // 更新可借款额度
            xUserInfo.setCanborrowAmount(xUserInfo.getCanborrowAmount() - xRecordLoan.getLoanAmount());
            xUserInfo.setUpdateTime(now);
            if (xRecordLoanDao.save(xRecordLoan) == 1 && xUserInfoDao.update(xUserInfo) == 1) {
                // 调用合作方接口借款
                borrowMoneyFromBank(xRecordLoan, card.getCardNo(), card.getBankNo(), card.getAccType());
            } else {
                xRecordLoan = null;
            }
            // endregion
        } finally {
            redisLock.releaseLock(xRecordLoan.getUserGid());
        }
        return xRecordLoan;
    }

    /**
     * 异步调用借款接口，借款成功、失败时更新数据库，并记录交易情况
     *
     * @param xRecordLoan
     */
    private void borrowMoneyFromBank(XRecordLoan xRecordLoan, String cardNo, String bankNo, String accType) {
        threadPoolService.execute(() -> {
            TransfersRsp transfersRsp = new TransfersRsp();
            //调用风控规则接口判断是否有借款规则
            Map<String, Object> params = new HashMap<>();
            params.put("userGid", xRecordLoan.getUserGid());
            params.put("flag", 1);
            String ret = restTemplate.getForObject(riskControlUrl, String.class, params);
            if ("true".equals(ret)) {
                logger.info("调用baokim接口，进行借款--用户:{},金额:{}", xRecordLoan.getUserGid(), xRecordLoan.getDueAmount());
                try {
                    transfersRsp = xapiService.transfers(bankNo,  // 银行代码
                            cardNo, // 借款方账号
                            xRecordLoan.getNetAmount(),
                            xRecordLoan.getMemo(),
                            accType);
                } catch (Exception ex) {
                    logger.info("调用借款接口发生异常了");
                    xRecordLoanDao.delete(xRecordLoan.getId());
                    return;
                }
                logger.info("调用baokim接口结束--");
                if (BaokimResponseStatus.TIMEOUT.getCode().equals(transfersRsp.getResponseCode())) {
                    transfersRsp = xapiService.transfersInfo(transfersRsp.getReferenceId());
                }
            } else {
                transfersRsp.setResponseCode(i18nService.getMessage("response.error.borrow.riskcheck.code"));
                transfersRsp.setResponseMessage(i18nService.getMessage("response.error.borrow.riskcheck.msg"));
            }
            confirmLoan(xRecordLoan, transfersRsp);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmLoan(XRecordLoan xRecordLoan, TransfersRsp transfersRsp) {
        Date now = new Date();
        xRecordLoan.setUpdateTime(now);
        if (BaokimResponseStatus.SUCCESS.getCode().equals(transfersRsp.getResponseCode())) {
            // 实际到账时间
            xRecordLoan.setLoanTime(now);
            // 应还款时间
            xRecordLoan.setDueTime(calcDueTime(now, xRecordLoan.getLoanPeriod()));
            // 实际到账金额
            xRecordLoan.setNetAmount(Long.parseLong(transfersRsp.getTransferAmount()));
            logger.info(transfersRsp.getRequestAmount().equals(transfersRsp.getTransferAmount()) ? "实际到账等于汇款的数目" : "实际到账小于需要汇款的数目");
            // 借款状态
            xRecordLoan.setLoanStatus(SysVariable.LOAN_STATUS_SUCCESS);
        } else {
            logger.info(transfersRsp.getResponseMessage());
            // 借款状态
            xRecordLoan.setLoanStatus(SysVariable.LOAN_STATUS_FAIL);
            // 借款失败原因
            xRecordLoan.setFailReason(transfersRsp.getResponseMessage());
            XUserInfo xUserInfo = xUserInfoDao.getByGid(xRecordLoan.getUserGid());
            xUserInfo.setCanborrowAmount(xUserInfo.getCanborrowAmount() + xRecordLoan.getLoanAmount());
            xUserInfoDao.update(xUserInfo);
        }
        xRecordLoanDao.update(xRecordLoan);
    }

    /**
     * 设置成当天的最后一秒
     *
     * @param date
     * @param plus
     * @return
     */
    private Date calcDueTime(Date date, int plus) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, plus);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    @Override
    public PageTableResponse borrowList(int limit, int num, String userGid) {
        PageTableRequest request = new PageTableRequest();
        request.setOffset(limit * (num - 1));
        request.setLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "update_time");
        params.put("userGid", userGid);
        request.setParams(params);
        return new PageTableHandler((a) -> xRecordLoanDao.count(a.getParams()),
                (a) -> xRecordLoanDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public XRecordLoan getRecordLoan(String gid, String userGid) {
        return xRecordLoanDao.getByGid(gid, userGid);
    }

    @Override
    public boolean calcOverDueFee() {
        List<XRecordLoan> xrlList = xRecordLoanDao.list(new HashMap<>(), null, null);
        // 滞纳金配置
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_OVERDUE_RATE));
        if (config == null) {
            logger.error("滞纳金配置获取出错");
            return false;
        }
        List<XRecordLoan> updateList = new ArrayList<>();
        Date now = new Date();
        try {
            for (XRecordLoan xrl : xrlList) {
                // 还没还清借款
                if (xrl.getLoanStatus() == SysVariable.LOAN_STATUS_SUCCESS &&
                        xrl.getRepaymentStatus() != SysVariable.REPAYMENT_STATUS_SUCCESS) {
                    // 逾期
                    if (now.getTime() > xrl.getDueTime().getTime()) {
                        // 逾期天数
                        long day = (now.getTime() - xrl.getDueTime().getTime()) / (3600 * 1000 * 24) + 1;

                        // 逾期计息
                        if (xrl.getOverdueInterestTotal() < xrl.getDueAmount()) {
                            // 每天利息
                            BigDecimal interestPerDay = xrl.getLoanRate().multiply(new BigDecimal(xrl.getDueAmount() / xrl.getLoanPeriod()));
                            // 总利息 + 一天利息
                            long interestTotalPlusDay = CommonUtil.longAddBigDecimal(xrl.getOverdueInterestTotal(), interestPerDay);
                            if (interestTotalPlusDay > xrl.getDueAmount()) {
                                xrl.setOverdueInterest(xrl.getOverdueInterest() + xrl.getDueAmount() - xrl.getOverdueInterestTotal());
                                xrl.setOverdueInterestTotal(xrl.getDueAmount());
                            } else {
                                xrl.setOverdueInterest(CommonUtil.longAddBigDecimal(xrl.getOverdueInterest(), interestPerDay));
                                xrl.setOverdueInterestTotal(interestTotalPlusDay);
                            }
                        }

                        // 逾期费用
                        if (day == 1) {
                            // 平台管理费 = 剩余本金 * 0.03
                            long manager = CommonUtil.longAddBigDecimal(xrl.getDueAmount(), new BigDecimal(config.get("0").toString()));
                            if (manager >= xrl.getDueAmount()) {
                                xrl.setOverdueFeeTotal(xrl.getDueAmount());
                                xrl.setOverdueFee(xrl.getDueAmount());
                            } else {
                                xrl.setOverdueFeeTotal(manager);
                                xrl.setOverdueFee(manager);
                            }
                        }
                        // 逾期滞纳金
                        if (xrl.getOverdueFeeTotal() < xrl.getDueAmount()) {
                            for (Object key : config.keySet()) {
                                BigDecimal value = new BigDecimal(config.get(key).toString());
                                long start = Long.parseLong(key.toString().split("~")[0]);
                                if (start != 0L) {
                                    long end = Long.parseLong(key.toString().split("~")[1]);
                                    if (day >= start && day <= end) {
                                        long feePerDay = CommonUtil.longMultiplyBigDecimal(xrl.getDueAmount(), value);
                                        if ((xrl.getOverdueFeeTotal() + feePerDay) <= xrl.getDueAmount()) {
                                            xrl.setOverdueFeeTotal(xrl.getOverdueFeeTotal() + feePerDay);
                                            xrl.setOverdueFee(xrl.getOverdueFee() + feePerDay);
                                        } else {
                                            xrl.setOverdueFee(xrl.getOverdueFee() + xrl.getDueAmount() - xrl.getOverdueFeeTotal());
                                            xrl.setOverdueFeeTotal(xrl.getDueAmount());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    updateList.add(xrl);
                }
            }
        } catch (Exception ex) {
            logger.error("滞纳金计算过程出错");
            return false;
        }
        if (!updateList.isEmpty()) {
            xRecordLoanDao.updateByBatch(updateList);
        }
        return true;
    }

    @Override
    public boolean calcOverDueFee2() {
        List<XRecordLoan> xrlList = xRecordLoanDao.list(new HashMap<>(), null, null);
        // 滞纳金配置
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_OVERDUE_RATE));
        if (config == null) {
            logger.error("滞纳金配置获取出错");
            return false;
        }
        List<XRecordLoan> updateList = new ArrayList<>();
        try {
            for (XRecordLoan xrl : xrlList) {
                // 还没还清借款
                if (xrl.getLoanStatus() == SysVariable.LOAN_STATUS_SUCCESS &&
                        xrl.getRepaymentStatus() != SysVariable.REPAYMENT_STATUS_SUCCESS) {
                    // 逾期
                    long now = System.currentTimeMillis();
                    if (now > xrl.getDueTime().getTime() && xrl.getDueAmount() > xrl.getOverdueFee()) {
                        // 逾期天数
                        long day = (now - xrl.getDueTime().getTime()) / (3600 * 1000 * 24) + 1;

                        long totalFee = 0L;

                        // 平台管理费
                        if (day == 1) {
                            // 平台管理费 = 剩余本金 * 0.03
                            totalFee = CommonUtil.longAddBigDecimal(xrl.getDueAmount(), new BigDecimal(config.get("0").toString()));
                        }

                        // 逾期计息
                        BigDecimal interestPerDay = xrl.getLoanRate().multiply(new BigDecimal(xrl.getDueAmount() / xrl.getLoanPeriod()));
                        totalFee = CommonUtil.longAddBigDecimal(totalFee, interestPerDay);

                        // 逾期滞纳金
                        for (Object key : config.keySet()) {
                            BigDecimal value = new BigDecimal(config.get(key).toString());
                            long start = Long.parseLong(key.toString().split("~")[0]);
                            if (start != 0L) {
                                long end = Long.parseLong(key.toString().split("~")[1]);
                                if (day >= start && day <= end) {
                                    long feePerDay = CommonUtil.longMultiplyBigDecimal(xrl.getDueAmount(), value);
                                    totalFee += feePerDay;
                                }
                            }
                        }
                        if (xrl.getOverdueFee() + totalFee >= xrl.getDueAmount()) {
                            xrl.setOverdueFee(xrl.getDueAmount());
                        } else {
                            xrl.setOverdueFee(xrl.getOverdueFee() + totalFee);
                        }
                        updateList.add(xrl);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("滞纳金计算过程出错");
            return false;
        }
        if (!updateList.isEmpty()) {
            //todo new 需要分批批量插入，200个一组
            xRecordLoanDao.updateByBatch(updateList);
        }
        return true;
    }

    @Override
    public long getSumLoanAmount(String userGid) {
        return xRecordLoanDao.getSumLoanAmount(userGid);
    }
}
