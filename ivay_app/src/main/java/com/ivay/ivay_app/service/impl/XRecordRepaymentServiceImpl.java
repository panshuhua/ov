package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.advice.BusinessException;
import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.dao.XRecordLoanDao;
import com.ivay.ivay_app.dao.XRecordRepaymentDao;
import com.ivay.ivay_app.dao.XUserInfoDao;
import com.ivay.ivay_app.dao.XVirtualAccountDao;
import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.ValVirtualAccountRsp;
import com.ivay.ivay_app.model.XRecordLoan;
import com.ivay.ivay_app.model.XRecordRepayment;
import com.ivay.ivay_app.model.XUserInfo;
import com.ivay.ivay_app.model.XVirtualAccount;
import com.ivay.ivay_app.service.ThreadPoolService;
import com.ivay.ivay_app.service.XRecordRepaymentService;
import com.ivay.ivay_app.service.XVirtualAccountService;
import com.ivay.ivay_app.table.PageTableHandler;
import com.ivay.ivay_app.table.PageTableRequest;
import com.ivay.ivay_app.table.PageTableResponse;
import com.ivay.ivay_app.utils.SysVariable;
import com.ivay.ivay_app.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XRecordRepaymentServiceImpl implements XRecordRepaymentService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");
    @Autowired
    private XRecordRepaymentDao xRecordRepaymentDao;

    @Autowired
    private XRecordLoanDao xRecordLoanDao;

    @Autowired
    private I18nService i18nService;

    @Autowired
    private ThreadPoolService threadPoolService;
    @Autowired
    private XUserInfoDao xUserInfoDao;

    @Autowired
    private XVirtualAccountDao xVirtualAccountDao;

    @Autowired
    private XVirtualAccountService xVirtualAccountService;

    @Override
    public XRecordRepayment getByGid(String repaymentGid, String userGid) {
        return xRecordRepaymentDao.getByGid(repaymentGid, userGid);
    }

    @Override
    public PageTableResponse list(int limit, int num, String userGid) {
        PageTableRequest request = new PageTableRequest();
        request.setOffset(limit * (num - 1));
        request.setLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "update_time");
        params.put("userGid", userGid);
        request.setParams(params);
        return new PageTableHandler((a) -> xRecordRepaymentDao.count(a.getParams()),
                (a) -> xRecordRepaymentDao.list(request.getParams(), request.getOffset(), request.getLimit())
        ).handle(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XVirtualAccount repaymentMoney(String orderGid, String userGid, String bankShortName, long repaymentAmount, Integer deductType) {
        XRecordLoan xRecordLoan = xRecordLoanDao.getByGid(orderGid, userGid);
        XVirtualAccount xVirtualAccount = xVirtualAccountService.selectByOrderId(xRecordLoan.getOrderId());
        if (SysVariable.REPAYMENT_STATUS_SUCCESS == xRecordLoan.getRepaymentStatus()) {
            //已经还清借款，不需要再次还款，直接返回虚拟账号
            //XVirtualAccount xVirtualAccount=xVirtualAccountService.selectByOrderId(xRecordLoan.getOrderId());
            return xVirtualAccount;
        }

        XRecordRepayment xRecordRepayment = new XRecordRepayment();
        // 用户gid
        xRecordRepayment.setUserGid(userGid);
        // 借款gid
        xRecordRepayment.setRecordLoanGid(orderGid);
        List<XRecordRepayment> result = xRecordRepaymentDao.getSelective(xRecordRepayment);
        // 还过款
        if (result != null && !result.isEmpty()) {
            for (XRecordRepayment x : result) {
                // 还款中，等待银行确认收款更新状态
                if (SysVariable.REPAYMENT_STATUS_DOING == x.getRepaymentStatus()) {
                    throw new BusinessException("有一笔还款正在等待银行处理");
                }
            }
        }
        // 还款gid
        xRecordRepayment.setGid(UUIDUtils.getUUID());
        // 还款类型
        xRecordRepayment.setRepaymentType(SysVariable.REPAYMENT_MODE_NORMAL);
        // 还款方式
        xRecordRepayment.setRepaymentWay(deductType);
        // 还款金额
        xRecordRepayment.setRepaymentAmount(repaymentAmount);
        // 逾期滞纳金 无用
        xRecordRepayment.setRepaymentOverdueFee(0L);
        // 还款状态
        xRecordRepayment.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_DOING);
        Date now = new Date();
        // 创建时间
        xRecordRepayment.setCreateTime(now);
        // 更新时间
        xRecordRepayment.setUpdateTime(now);
        //XVirtualAccount xVirtualAccount=new XVirtualAccount();
        if (xRecordRepaymentDao.save(xRecordRepayment) == 1) {
            //repayMoneyToBank(xRecordLoan, xRecordRepayment);
            if (xVirtualAccount == null) {
                xVirtualAccount = createVirtualCount(xRecordLoan, xRecordRepayment);
                if ("200".equals(xVirtualAccount.getResponseCode())) {
                    confirmRepayment(xRecordLoan, xRecordRepayment);
                }
            } else {
                xVirtualAccount = updateVirtualCount(xVirtualAccount, xRecordRepayment.getRepaymentAmount());
                if ("200".equals(xVirtualAccount.getResponseCode())) {
                    confirmRepayment(xRecordLoan, xRecordRepayment);
                }
            }
        } else {
            xRecordRepayment = null;
            xVirtualAccount = null;
        }
        return xVirtualAccount;
    }

    /**
     * 异步调用还款接口，还款成功、失败时更新数据库，并记录交易情况
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void repayMoneyToBank(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment) {
        threadPoolService.execute(() -> {
            createVirtualCount(xRecordLoan, xRecordRepayment);
            confirmRepayment(xRecordLoan, xRecordRepayment);
        });
    }

    //@Transactional(rollbackFor = Exception.class)
    public XVirtualAccount createVirtualCount(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment) {
        ValVirtualAccountRsp valVirtualAccountRsp = xVirtualAccountService.addVirtualAccount(xRecordLoan, xRecordRepayment);
        String responseCode = valVirtualAccountRsp.getResponseCode();
        String responseMsg = valVirtualAccountRsp.getResponseMessage();
        XVirtualAccount xVirtualAccount = new XVirtualAccount();
        if (BaokimResponseStatus.CollectionSuccess.getCode().equals(responseCode)) {
            // 保存虚拟账号信息
            xVirtualAccount.setAccName(valVirtualAccountRsp.getAccName());
            xVirtualAccount.setAccNo(valVirtualAccountRsp.getAccNo());
            xVirtualAccount.setClientidNo(valVirtualAccountRsp.getClientIdNo());
            xVirtualAccount.setIssuedDate(valVirtualAccountRsp.getIssuedDate());
            xVirtualAccount.setIssuedPlace(valVirtualAccountRsp.getIssuedPlace());
            if (valVirtualAccountRsp.getCollectAmount() != null) {
                xVirtualAccount.setCollectAmount(Integer.parseInt(valVirtualAccountRsp.getCollectAmount()));
            }
            xVirtualAccount.setOrderId(xRecordLoan.getOrderId());
            xVirtualAccount.setExpireDate(valVirtualAccountRsp.getExpireDate());
            xVirtualAccount.setAccountType(Integer.parseInt(valVirtualAccountRsp.getAccountType()));
            xVirtualAccount.setCreateTime(new Date());
            xVirtualAccount.setEnableFlag("Y");
            xVirtualAccount.setRequestId(valVirtualAccountRsp.getRequestId());
            xVirtualAccount.setResponseCode(responseCode);
            xVirtualAccount.setResponseMessage(responseMsg);
            xVirtualAccountDao.insert(xVirtualAccount);
            logger.info("创建虚拟账号成功");

        } else {
            logger.info("创建虚拟账号失败，返回状态码：{}，错误信息：{}", valVirtualAccountRsp.getResponseCode(), valVirtualAccountRsp.getResponseMessage());
            xVirtualAccount.setRequestId(valVirtualAccountRsp.getRequestId());
            xVirtualAccount.setResponseCode(responseCode);
            xVirtualAccount.setResponseMessage(responseMsg);
        }
        return xVirtualAccount;
    }

    public XVirtualAccount updateVirtualCount(XVirtualAccount xVirtualAccount, Long collectAmount) {
        ValVirtualAccountRsp valVirtualAccountRsp = xVirtualAccountService.updateXVirtualAccount(xVirtualAccount, collectAmount);
        String responseCode = valVirtualAccountRsp.getResponseCode();
        String responseMsg = valVirtualAccountRsp.getResponseMessage();
        if (BaokimResponseStatus.CollectionSuccess.getCode().equals(responseCode)) {
            // 更新虚拟账号信息
            if (valVirtualAccountRsp.getCollectAmount() != null) {
                Integer colAmount = Integer.parseInt(valVirtualAccountRsp.getCollectAmount());
                xVirtualAccount.setCollectAmount(colAmount);
            }
            xVirtualAccount.setUpdateTime(new Date());
            xVirtualAccount.setRequestId(valVirtualAccountRsp.getRequestId());
            xVirtualAccount.setResponseCode(responseCode);
            xVirtualAccount.setResponseMessage(responseMsg);
            xVirtualAccountDao.update(xVirtualAccount);
            logger.info("更新虚拟账号信息成功");

        } else {
            logger.info("更新虚拟账号信息失败，返回状态码：{}，错误信息：{}", valVirtualAccountRsp.getResponseCode(), valVirtualAccountRsp.getResponseMessage());
            xVirtualAccount.setRequestId(valVirtualAccountRsp.getRequestId());
            xVirtualAccount.setResponseCode(responseCode);
            xVirtualAccount.setResponseMessage(responseMsg);
        }
        return xVirtualAccount;
    }

    @Override
    //@Transactional(rollbackFor = Exception.class)
    public void confirmRepayment(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment) {
        Date now = new Date();
        xRecordRepayment.setUpdateTime(now);
        if ("200".equals(BaokimResponseStatus.SUCCESS.getCode())) {
            // 实际扣款时间
            xRecordRepayment.setEndTime(now);
            // 还款单的还款状态
            xRecordRepayment.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_SUCCESS);

            // 获取用户可借款金额
            XUserInfo xUserInfo = xUserInfoDao.getByGid(xRecordLoan.getUserGid());
            // 更新借款表的还款额度和滞纳金等
            long diff = xRecordRepayment.getRepaymentAmount() - xRecordLoan.getDueAmount();
            if (diff < 0) {
                // 还有本金没还完
                xRecordLoan.setDueAmount(-diff);
                xRecordLoan.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_DOING);
                // 更新可借额度
                xUserInfo.setCanborrowAmount(xUserInfo.getCanborrowAmount() + xRecordRepayment.getRepaymentAmount());
            } else {
                // 更新可借额度
                xUserInfo.setCanborrowAmount(xUserInfo.getCanborrowAmount() + xRecordLoan.getDueAmount());
                xRecordLoan.setDueAmount(0L);
                if (xRecordLoan.getOverdueFee() + xRecordLoan.getOverdueInterest() <= diff) {
                    // 还完本金 + 逾期费用
                    xRecordLoan.setOverdueFee(0L);
                    xRecordLoan.setOverdueInterest(0L);
                    xRecordLoan.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_SUCCESS);
                } else {
                    // 还有逾期费用没还
                    // 先还利息还是滞纳金
                    if (xRecordLoan.getOverdueInterest() >= diff) {
                        xRecordLoan.setOverdueInterest(xRecordLoan.getOverdueInterest() - diff);
                    } else {
                        xRecordLoan.setOverdueInterest(0L);
                        xRecordLoan.setOverdueFee(diff - xRecordLoan.getOverdueInterest());
                    }
                    xRecordLoan.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_DOING);
                }
            }
            // 最后一次还款时间
            xRecordLoan.setLastRepaymentTime(now);
            xRecordLoan.setUpdateTime(now);
            xRecordLoanDao.update(xRecordLoan);
            // 更新用户表可借额度
            xUserInfoDao.updateCanborrowAmount(xUserInfo.getCanborrowAmount(), xRecordLoan.getUserGid());
        } else {
            xRecordRepayment.setUpdateTime(now);
            // 还款失败原因
            xRecordRepayment.setFailReason("还款失败");
            // 还款状态
            xRecordRepayment.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_FAIL);
        }
        xRecordRepaymentDao.update(xRecordRepayment);
    }

    XRecordRepayment repaymentSuccess(String orderGid, String userGid) {
        XRecordRepayment xRecordRepayment = xRecordRepaymentDao.getByGid(orderGid, userGid);
        xRecordRepayment.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_SUCCESS);
        Date now = new Date();
        xRecordRepayment.setEndTime(now);
        xRecordRepayment.setUpdateTime(now);
        return xRecordRepaymentDao.update(xRecordRepayment) == 1 ? xRecordRepayment : null;
    }
}
