package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.*;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.*;
import com.ivay.ivay_repository.dao.master.XLoanRateDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserBankcardInfoDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dto.XOverDueFee;
import com.ivay.ivay_repository.dto.XTimeoutTransferInfo;
import com.ivay.ivay_repository.dto.XUserCardAndBankInfo;
import com.ivay.ivay_repository.model.XAppEvent;
import com.ivay.ivay_repository.model.XLoanRate;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XUserInfo;
import org.apache.commons.lang3.StringUtils;
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
    private static final Logger logger = LoggerFactory.getLogger(XRecordLoanService.class);
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

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private XAppEventService xAppEventService;

    @Autowired
    private XFirebaseNoticeService xFirebaseNoticeService;

    @Value("${risk_control_url}")
    private String riskControlUrl;

    @Autowired
    private RedisLock redisLock;

    /**
     * 提交借款申请
     *
     * @param xRecordLoan
     * @param password
     * @return 返回借款失败的理由
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String borrowMoney(XRecordLoan xRecordLoan, String password) {
        String result = null;
        // 校验最小可借额度
        if (xRecordLoan.getLoanAmount() < SysVariable.LOAN_MIN_AMOUNT) {
            throw new BusinessException(i18nService.getMessage("response.error.borrow.maxAmount.code"),
                    i18nService.getMessage("response.error.borrow.maxAmount.msg"));
        }
        // 校验账户余额是否足够

        // 加锁： 防重复提交
        if (!redisLock.tryBorrowLock(xRecordLoan.getUserGid())) {
            throw new BusinessException(i18nService.getMessage("response.error.borrow.repeat.code"),
                    i18nService.getMessage("response.error.borrow.repeat.msg"));
        }
        try {
            // region -- 借款
            XUserInfo xUserInfo = xUserInfoDao.getByUserGid(xRecordLoan.getUserGid());
            if (xUserInfo == null) {
                throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                        i18nService.getMessage("response.error.user.checkgid.msg"));
            }
            // 校验交易密码
            if (!bCryptPasswordEncoder.matches(password, xUserInfo.getTransPwd())) {
                throw new BusinessException(i18nService.getMessage("response.error.borrow.tranpwd.code"),
                        i18nService.getMessage("response.error.borrow.tranpwd.msg"));
            }
            // 授信失败不允许借款
            if (StringUtils.isEmpty(xUserInfo.getUserStatus()) || "78".contains(xUserInfo.getUserStatus())) {
                throw new BusinessException(i18nService.getMessage("response.error.user.authority.code"),
                        i18nService.getMessage("response.error.user.authority.msg"));
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
                    if (xrl.getLoanStatus() == SysVariable.LOAN_STATUS_SUCCESS
                            && xrl.getRepaymentStatus() != SysVariable.REPAYMENT_STATUS_SUCCESS
                            && now.getTime() > xrl.getDueTime().getTime()) {
                        logger.info("逾期记录：{},{}", xrl.getUserGid(), xrl.getOrderId());
                        throw new BusinessException(i18nService.getMessage("response.error.borrow.hasOverDue.code"),
                                i18nService.getMessage("response.error.borrow.hasOverDue.msg"));
                    }
                }
            }

            List<XUserCardAndBankInfo> cardList = xUserBankcardInfoDao.getCardAndBankByGid(xRecordLoan.getUserGid(), xRecordLoan.getBankcardGid());
            // 校验银行卡
            if (cardList.size() == 0) {
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
            xRecordLoan.setFee(CommonUtil.longMultiplyBigDecimal(xRecordLoan.getLoanAmount(), xRecordLoan.getLoanRate())
                    - xRecordLoan.getInterest());
            // 实际到账金额：总金额-手续费-砍頭息
            xRecordLoan.setNetAmount(xRecordLoan.getLoanAmount() - xRecordLoan.getFee() - xRecordLoan.getInterest());
            // 应还金额
            xRecordLoan.setDueAmount(xRecordLoan.getLoanAmount());
            // 应还逾期滞纳金
            xRecordLoan.setOverdueFee(0L);
            // 总逾期滞纳金
            xRecordLoan.setOverdueFeeTotal(0L);
            // 应还逾期利息
            xRecordLoan.setOverdueInterest(0L);
            // 多还金额
            xRecordLoan.setMoreRepaymentAmount(0L);
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
                result = borrowMoneyFromBank(xRecordLoan, cardList.get(0).getCardNo(), cardList.get(0).getBankNo(),
                        cardList.get(0).getAccType());
            } else {
                logger.error("借款失败：" + xRecordLoan.getOrderId());
            }
            // endregion
        } finally {
            redisLock.releaseBorrowLock(xRecordLoan.getUserGid());
        }
        return result;
    }

    /**
     * 异步调用借款接口，借款成功、失败时更新数据库，并记录交易情况
     *
     * @param xRecordLoan
     * @param cardNo      借款方账号
     * @param bankNo      银行代码
     * @param accType
     * @return 返回借款失败原因
     */
    private String borrowMoneyFromBank(XRecordLoan xRecordLoan, String cardNo, String bankNo, String accType) {
        // 账号余额不足
        if (xapiService.getCanborrowBalance() < xRecordLoan.getNetAmount()) {
            TransfersRsp transfersRsp = new TransfersRsp();
            transfersRsp.setResponseCode(BaokimResponseStatus.FAIL.getCode());
            transfersRsp.setResponseMessage("Not enough balance");
            confirmLoan(xRecordLoan, transfersRsp);
            return "response.error.borrow.notEnoughBalance";
        } else {
            threadPoolService.execute(() -> {
                // 调用风控规则接口判断是否有借款规则
                Map<String, Object> params = new HashMap<>();
                params.put("userGid", xRecordLoan.getUserGid());
                params.put("flag", SysVariable.RISK_TYPE_LOAN);
                String loanQualify;
                try {
                    loanQualify = restTemplate.getForObject(riskControlUrl, String.class, params);
                } catch (Exception ex) {
                    logger.error(ex.toString());
                    loanQualify = "借款资格接口调用异常";
                }

                TransfersRsp transfersRsp = new TransfersRsp();
                if (StringUtils.isEmpty(loanQualify)) {
                    boolean apiFlag = true;
                    logger.info("调用baokim接口，进行借款--用户:{},金额:{}", xRecordLoan.getUserGid(), xRecordLoan.getDueAmount());
                    try {
                        transfersRsp = xapiService.transfers(bankNo, cardNo, xRecordLoan.getNetAmount(),
                                xRecordLoan.getMemo(), accType, xRecordLoan.getGid());
                    } catch (Exception ex) {
                        apiFlag = false;
                        return;
                    } finally {
                        sysLogService.save(xRecordLoan.getUserGid(), null, "借款", apiFlag, transfersRsp.getResponseMessage(),
                                transfersRsp.getResponseCode());
                        if (apiFlag) {
                            logger.info("调用baokim借款接口结束--");
                        } else {
                            logger.info("调用baokim借款接口发生异常--");
                            xRecordLoanDao.delete(xRecordLoan.getId());
                        }
                    }
                } else {
                    transfersRsp.setResponseCode(i18nService.getMessage("response.error.borrow.riskcheck.code"));
                    transfersRsp.setResponseMessage("风控规则不通过: " + loanQualify);
                }
                confirmLoan(xRecordLoan, transfersRsp);
            });
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmLoan(XRecordLoan xRecordLoan, TransfersRsp transfersRsp) {
        Date now = new Date();
        xRecordLoan.setUpdateTime(now);
        XUserInfo xUserInfo = xUserInfoDao.getByUserGid(xRecordLoan.getUserGid());
        xUserInfo.setUpdateTime(now);

        XAppEvent xAppEvent = new XAppEvent();
        boolean appEventFlag = true;

        if (BaokimResponseStatus.SUCCESS.getCode().equals(transfersRsp.getResponseCode())) {
            // 借款成功
            Date loanTime = DateUtils.stringToDate_YYYY_MM_DD_HH_MM_SS(transfersRsp.getTransactionTime());
            // 实际到账时间
            xRecordLoan.setLoanTime(loanTime);
            // 应还款时间
            xRecordLoan.setDueTime(calcDueTime(loanTime, xRecordLoan.getLoanPeriod()));
            // 实际到账金额
            xRecordLoan.setNetAmount(Long.parseLong(transfersRsp.getTransferAmount()));
            // 借款状态
            xRecordLoan.setLoanStatus(SysVariable.LOAN_STATUS_SUCCESS);
            // 用户状态
            if (SysVariable.USER_STATUS_LOAN_SUCCESS.equals(xUserInfo.getUserStatus())) {
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_LOAN_REPEATEDLY);
            } else {
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_LOAN_SUCCESS);
            }
            xAppEvent.setIsSuccess(SysVariable.APP_EVENT_SUCCESS);

            // 发送借款成功通知
            xFirebaseNoticeService.sendLoanSuccessNotice(xRecordLoan, xUserInfo);
        } else if (BaokimResponseStatus.TIMEOUT.getCode().equals(transfersRsp.getResponseCode())) {
            // 借款超时
            xRecordLoan.setFailReason(transfersRsp.getResponseMessage());
            appEventFlag = false;
        } else {
            logger.info(transfersRsp.getResponseMessage());
            // 借款状态
            xRecordLoan.setLoanStatus(SysVariable.LOAN_STATUS_FAIL);
            // 借款失败原因
            xRecordLoan.setFailReason(transfersRsp.getResponseMessage());
            xUserInfo.setCanborrowAmount(xUserInfo.getCanborrowAmount() + xRecordLoan.getLoanAmount());
            xAppEvent.setIsSuccess(SysVariable.APP_EVENT_FAIL);
        }
        if (xUserInfo.getCanborrowAmount() > xUserInfo.getCreditLine()) {
            xUserInfo.setCanborrowAmount(xUserInfo.getCreditLine());
        }
        xUserInfoDao.update(xUserInfo);
        xRecordLoanDao.update(xRecordLoan);

        // 记录待上报的app事件
        if (appEventFlag) {
            xAppEvent.setType(SysVariable.APP_EVENT_LOAN);
            xAppEvent.setGid(xRecordLoan.getOrderId());
            xAppEventService.save(xAppEvent);
        }
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
        cal.set(Calendar.MILLISECOND, 0);
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
        return new PageTableHandler(a -> xRecordLoanDao.count(a.getParams()),
                a -> xRecordLoanDao.list(a.getParams(), a.getOffset(), a.getLimit())).handle(request);
    }

    @Override
    public XRecordLoan getRecordLoan(String gid, String userGid) {
        return xRecordLoanDao.getByGid(gid, userGid);
    }

    /**
     * 逾期费用 = 逾期计息 + 逾期滞纳金，加起来不能超过本金
     *
     * @return
     */
    @Override
    public boolean calcOverDueFee() {
        List<XRecordLoan> xrlList = xRecordLoanDao.list(new HashMap<>(), null, null);
        // 滞纳金配置
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_OVERDUE_RATE));
        List<XRecordLoan> updateList = new ArrayList<>();
        try {
            for (XRecordLoan xrl : xrlList) {
                // 还没还清借款
                if (xrl.getLoanStatus() == SysVariable.LOAN_STATUS_SUCCESS
                        && xrl.getRepaymentStatus() != SysVariable.REPAYMENT_STATUS_SUCCESS) {
                    // 逾期
                    long now = System.currentTimeMillis();
                    if (now > xrl.getDueTime().getTime() && xrl.getDueAmount() > xrl.getOverdueFee()) {
                        // 逾期天数
                        long day = (now - xrl.getDueTime().getTime()) / (3600 * 1000 * 24) + 1;
                        logger.info("用户: {}, 逾期天数: {}", xrl.getUserGid(), day);

                        long totalFee = 0L;

                        // 平台管理费
                        if (day == 1) {
                            // 平台管理费 = 剩余本金 * 0.03
                            totalFee = CommonUtil.longMultiplyBigDecimal(xrl.getDueAmount(),
                                    new BigDecimal(config.get("0").toString()));
                            logger.info("用户: {}, 平台管理费: {}", xrl.getUserGid(), totalFee);
                        }

                        // 逾期计息
                        BigDecimal interestPerDay =
                                xrl.getLoanRate().multiply(new BigDecimal(xrl.getDueAmount() / xrl.getLoanPeriod()));
                        totalFee = CommonUtil.longAddBigDecimal(totalFee, interestPerDay);
                        logger.info("用户: {}, 一天的逾期计息: {}", xrl.getUserGid(), interestPerDay.toString());

                        // 逾期滞纳金
                        BigDecimal value;
                        for (Object key : config.keySet()) {
                            value = new BigDecimal(config.get(key).toString());
                            long start = Long.parseLong(key.toString().split("~")[0]);
                            if (start != 0L) {
                                long end = Long.parseLong(key.toString().split("~")[1]);
                                if (day >= start && day <= end) {
                                    long feePerDay = CommonUtil.longMultiplyBigDecimal(xrl.getDueAmount(), value);
                                    totalFee += feePerDay;
                                    logger.info("用户: {}, 一天的逾期滞纳金: {}", xrl.getUserGid(), feePerDay);
                                }
                            }
                        }
                        if (xrl.getOverdueFee() + totalFee >= xrl.getDueAmount()) {
                            xrl.setOverdueFeeTotal(xrl.getOverdueFeeTotal() + xrl.getDueAmount() - xrl.getOverdueFee());
                            xrl.setOverdueFee(xrl.getDueAmount());
                        } else {
                            xrl.setOverdueFeeTotal(xrl.getOverdueFeeTotal() + totalFee);
                            xrl.setOverdueFee(xrl.getOverdueFee() + totalFee);
                        }
                        logger.info("用户: {}, 总逾期费用: {}", xrl.getUserGid(), xrl.getOverdueFee());
                        updateList.add(xrl);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("滞纳金计算过程出错");
            return false;
        }
        if (!updateList.isEmpty()) {
            // 批量更新，不需要割列表
            // 批量插入的话，一个sql长度限制为4M
            xRecordLoanDao.updateByBatch(updateList);
        }
        return true;
    }

    /**
     * 计算总逾期费用
     *
     * @param dueAmount 剩余本金
     * @param day       逾期天数
     * @return
     */
    @Override
    public long calcOverDueFee(long dueAmount, int day, BigDecimal loanRate, int loanPeriod) {
        if (dueAmount <= 0 || day <= 0) {
            return 0;
        }

        // 滞纳金配置
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_OVERDUE_RATE));

        // 平台管理费 = 剩余本金 * 0.03
        long totalFee =
                totalFee = CommonUtil.longMultiplyBigDecimal(dueAmount, new BigDecimal(config.get("0").toString()));

        // 逾期计息
        BigDecimal interestPer = loanRate.multiply(new BigDecimal(dueAmount / loanPeriod * day));
        totalFee = CommonUtil.longAddBigDecimal(totalFee, interestPer);

        // 逾期滞纳金
        BigDecimal value;
        for (Object key : config.keySet()) {
            value = new BigDecimal(config.get(key).toString());
            long start = Long.parseLong(key.toString().split("~")[0]);
            if (start != 0L) {
                long end = Long.parseLong(key.toString().split("~")[1]);
                if (day >= start) {
                    long feePerDay = CommonUtil.longMultiplyBigDecimal(dueAmount, value);
                    if (day >= end) {
                        totalFee += feePerDay * (end - start + 1);
                    } else {
                        totalFee += feePerDay * (day - start + 1);
                    }
                }
            }
        }
        if (totalFee > dueAmount) {
            totalFee = dueAmount;
        }
        return totalFee;
    }

    /**
     * 计算逾期一天的滞纳金
     *
     * @param list
     */
    @Override
    public void calcOverDueFeeFirstDay(List<XOverDueFee> list) {
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_OVERDUE_RATE));
        // 逾期平台管理费率
        BigDecimal manageFeeRate = new BigDecimal(config.get("0").toString());

        // 逾期滞纳金费率
        BigDecimal feeRate = new BigDecimal(0);
        for (Object key : config.keySet()) {
            long start = Long.parseLong(key.toString().split("~")[0]);
            if (start == 1L) {
                feeRate = new BigDecimal(config.get(key).toString());
            }
        }

        for (XOverDueFee x : list) {
            // 逾期平台管理费
            long totalFee = CommonUtil.longMultiplyBigDecimal(x.getDueAmount(), manageFeeRate);
            // 逾期计息
            BigDecimal interestPerDay = x.getLoanRate().multiply(new BigDecimal(x.getDueAmount() / x.getLoanPeriod()));
            totalFee = CommonUtil.longAddBigDecimal(totalFee, interestPerDay);
            // 逾期滞纳金
            totalFee += CommonUtil.longMultiplyBigDecimal(x.getDueAmount(), feeRate);
            if (totalFee >= x.getDueAmount()) {
                x.setOverdueFee(x.getDueAmount());
            } else {
                x.setOverdueFee(totalFee);
            }
        }
    }

    @Override
    public long getSumLoanAmount(String userGid) {
        return xRecordLoanDao.getSumLoanAmount(userGid);
    }

    /**
     * 查看借款超时的到账信息
     */
    @Override
    public void timeoutTransferInfo() {
        List<XTimeoutTransferInfo> timeouts = xRecordLoanDao.getTimeoutTransfer();
        for (XTimeoutTransferInfo x : timeouts) {
            // 出现超时，查询交易状态
            TransfersRsp rsp = xapiService.transfersInfo(x.getReferenceId(), x.getLoanGid());
            if (BaokimResponseStatus.SUCCESS.getCode().equals(rsp.getResponseCode())
                    || BaokimResponseStatus.FAIL.getCode().equals(rsp.getResponseCode())) {
                XRecordLoan xrl = xRecordLoanDao.getByLoanGid(x.getLoanGid());
                if (xrl != null) {
                    confirmLoan(xrl, rsp);
                }
            }
        }
    }
}
