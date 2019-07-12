package com.ivay.ivay_app.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.ValVirtualAccountRsp;
import com.ivay.ivay_app.service.SysLogService;
import com.ivay.ivay_app.service.ThreadPoolService;
import com.ivay.ivay_app.service.XFirebaseNoticeService;
import com.ivay.ivay_app.service.XRecordRepaymentService;
import com.ivay.ivay_app.service.XVirtualAccountService;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.enums.CollectionRepayStatusEnum;
import com.ivay.ivay_common.enums.CollectionStatusEnum;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_common.utils.UUIDUtils;
import com.ivay.ivay_repository.dao.master.XCollectionTaskDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XRecordRepaymentDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dao.master.XVirtualAccountDao;
import com.ivay.ivay_repository.model.XCollectionTask;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XRecordRepayment;
import com.ivay.ivay_repository.model.XUserInfo;
import com.ivay.ivay_repository.model.XVirtualAccount;

@Service
public class XRecordRepaymentServiceImpl implements XRecordRepaymentService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private XRecordRepaymentDao xRecordRepaymentDao;

    @Autowired
    private XRecordLoanDao xRecordLoanDao;

    @Autowired
    private ThreadPoolService threadPoolService;

    @Autowired
    private XUserInfoDao xUserInfoDao;

    @Autowired
    private XVirtualAccountDao xVirtualAccountDao;

    @Autowired
    private XVirtualAccountService xVirtualAccountService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private XFirebaseNoticeService xFirebaseNoticeService;

    @Autowired
    private XCollectionTaskDao xCollectionTaskDao;

    @Value("${repayment_success_post_handle_url}")
    private String repayment_success_post_handle_url;

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
        return new PageTableHandler(a -> xRecordRepaymentDao.count(a.getParams()),
            a -> xRecordRepaymentDao.list(request.getParams(), request.getOffset(), request.getLimit()))
                .handle(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XVirtualAccount repaymentMoney(String loanGid, String userGid, String bankShortName, long repaymentAmount,
        Integer deductType) {
        XRecordLoan xRecordLoan = xRecordLoanDao.getByGid(loanGid, userGid);

        XVirtualAccount xVirtualAccount = xVirtualAccountService.selectByOrderId(xRecordLoan.getOrderId());
        // 查询正在还款中的记录
        XRecordRepayment xRecordRepayment2 = xRecordRepaymentDao.getXRecordRepaymentByOrderId(xRecordLoan.getOrderId());

        // 正在还款中，并且虚拟账号不为空时，不需要创建虚拟账号（单次还款，不需要修改虚拟账号），直接返回
        if (xRecordRepayment2 != null && xVirtualAccount != null) {
            return xVirtualAccount;
        }

        if (SysVariable.REPAYMENT_STATUS_SUCCESS == xRecordLoan.getRepaymentStatus()) {
            // 已经还清借款，不需要再次还款，直接返回虚拟账号
            return xVirtualAccount;
        }

        XRecordRepayment xRecordRepayment = new XRecordRepayment();
        // 用户gid
        xRecordRepayment.setUserGid(userGid);
        // 订单id
        xRecordRepayment.setOrderId(xRecordLoan.getOrderId());
        // 借款gid
        xRecordRepayment.setRecordLoanGid(loanGid);
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

        if (xRecordRepayment2 == null) {
            if (xVirtualAccount == null) {
                xVirtualAccount = createVirtualCount(xRecordLoan, xRecordRepayment);
            } else {
                xVirtualAccount = updateVirtualCount(xVirtualAccount, xRecordRepayment);
            }

        } else {
            xRecordRepayment = xRecordRepayment2;
            if (xVirtualAccount == null) {
                xVirtualAccount = createVirtualCount(xRecordLoan, xRecordRepayment);
            } else {
                xVirtualAccount = updateVirtualCount(xVirtualAccount, xRecordRepayment);
            }
        }

        return xVirtualAccount;
    }

    public XVirtualAccount createVirtualCount(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment) {
        ValVirtualAccountRsp valVirtualAccountRsp =
            xVirtualAccountService.addVirtualAccount(xRecordLoan, xRecordRepayment);
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

            // 更新还款状态为还款中
            // 成功时在回调处记录

            logger.info("创建虚拟账号成功");
            sysLogService.save(xRecordLoan.getUserGid(), null, "还款-创建虚拟账号", true, responseMsg, responseCode);
        } else {
            logger.info("创建虚拟账号失败，返回状态码：{}，错误信息：{}", responseCode, responseMsg);
            sysLogService.save(xRecordLoan.getUserGid(), null, "还款-创建虚拟账号", false, responseMsg, responseCode);
            xVirtualAccount.setRequestId(valVirtualAccountRsp.getRequestId());
            xVirtualAccount.setResponseCode(responseCode);
            xVirtualAccount.setResponseMessage(responseMsg);
            xVirtualAccount.setCreateTime(new Date());
            xVirtualAccount.setUpdateTime(new Date());
            xVirtualAccount.setEnableFlag("Y");
            xVirtualAccountDao.insert(xVirtualAccount);
            // 更新还款状态为还款失败
            String failReason = "创建虚拟账号信息失败，返回状态码：" + responseCode + "，错误信息：" + responseMsg;
            xRecordRepayment.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_FAIL);
            xRecordRepayment.setFailReason(failReason);
            xRecordRepaymentDao.save(xRecordRepayment); // 失败时，记录失败信息
        }
        return xVirtualAccount;
    }

    public XVirtualAccount updateVirtualCount(XVirtualAccount xVirtualAccount, XRecordRepayment xRecordRepayment) {
        Long collectAmount = xRecordRepayment.getRepaymentAmount();
        ValVirtualAccountRsp valVirtualAccountRsp =
            xVirtualAccountService.updateXVirtualAccount(xVirtualAccount, collectAmount);
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
            sysLogService.save(xRecordRepayment.getUserGid(), null, "还款-更新虚拟账号", true, responseMsg, responseCode);
        } else {
            logger.info("更新虚拟账号信息失败，返回状态码：{}，错误信息：{}", responseCode, responseMsg);
            sysLogService.save(xRecordRepayment.getUserGid(), null, "还款-更新虚拟账号", false, responseMsg, responseCode);
            xVirtualAccount.setRequestId(valVirtualAccountRsp.getRequestId());
            xVirtualAccount.setResponseCode(responseCode);
            xVirtualAccount.setResponseMessage(responseMsg);
            xVirtualAccount.setCreateTime(new Date());
            xVirtualAccount.setUpdateTime(new Date());
            xVirtualAccount.setEnableFlag("Y");
            // 失败时只能新增一条数据记录错误信息，不能修改数据，否则会跟baokim的数据不一致
            xVirtualAccountDao.insert(xVirtualAccount);
            // 更新还款状态为还款失败
            String failReason = "更新虚拟账号信息失败，返回状态码：" + responseCode + "，错误信息：" + responseMsg;
            xRecordRepayment.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_FAIL);
            xRecordRepayment.setFailReason(failReason);
            xRecordRepaymentDao.save(xRecordRepayment); // 失败时，记录失败信息
        }
        return xVirtualAccount;
    }

    @Override
    public void confirmRepayment(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment, String responseCode) {
        Date now = new Date();
        xRecordRepayment.setUpdateTime(now);
        logger.info("进入还款更新状态方法-------------------------");
        logger.info("baokim还款返回:{}", responseCode);
        if (BaokimResponseStatus.SUCCESS.getCode().equals(responseCode)) {
            logger.info("{}: 还款金额:{}, 应还本金:{},应还利息:{}", xRecordRepayment.getOrderId(),
                xRecordRepayment.getRepaymentAmount(), xRecordLoan.getDueAmount(),
                xRecordLoan.getOverdueFee() + xRecordLoan.getOverdueInterest());
            // 获取用户可借款金额
            XUserInfo xUserInfo = xUserInfoDao.getByGid(xRecordLoan.getUserGid());
            // 更新借款表的还款额度和滞纳金等
            long diff = xRecordRepayment.getRepaymentAmount() - xRecordLoan.getDueAmount();

            // 获取用户关联的催收账单
            XCollectionTask xCollectionTask = xCollectionTaskDao.findNewCollectionByUserGid(xUserInfo.getUserGid());

            if (diff < 0) {
                // 还有本金没还完
                // 首先：更新可借额度
                xUserInfo.setCanborrowAmount(xUserInfo.getCanborrowAmount() + xRecordRepayment.getRepaymentAmount());
                // 然后：更新剩余本金
                xRecordLoan.setDueAmount(-diff);
                // 还款单的还款状态
                xRecordLoan.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_DOING);
                // 更新任务中的追回本金
                if (null != xCollectionTask) {
                    xCollectionTask.setCollectionAmount(
                        xCollectionTask.getCollectionAmount() + xRecordRepayment.getRepaymentAmount());
                    xCollectionTask.setCollectionRepayStatus(CollectionRepayStatusEnum.UNDER_REPAYING.getStatus());
                }
                logger.info("{}: 还有本金没还完:{}", xRecordRepayment.getOrderId(), xRecordLoan.getDueAmount());
            } else {
                // 本金已还完
                // 首先：更新可借额度, 注意顺序
                xUserInfo.setCanborrowAmount(xUserInfo.getCanborrowAmount() + xRecordLoan.getDueAmount());
                // 再更新剩余本金为零
                xRecordLoan.setDueAmount(0L);

                if (null != xCollectionTask) {
                    // 更新任务中的追回本金
                    xCollectionTask.setCollectionAmount(
                        xCollectionTask.getCollectionAmount() + xRecordRepayment.getRepaymentAmount());
                }

                if (xRecordLoan.getOverdueFee() + xRecordLoan.getOverdueInterest() <= diff) {
                    // 记录或许多还的金额
                    xRecordLoan
                        .setMoreRepaymentAmount(diff - xRecordLoan.getOverdueFee() - xRecordLoan.getOverdueInterest());
                    // 还清全部逾期费用
                    xRecordLoan.setOverdueFee(0L);
                    xRecordLoan.setOverdueInterest(0L);
                    xRecordLoan.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_SUCCESS);

                    if (null != xCollectionTask) {
                        // 更新任务中的追回逾期利息
                        xCollectionTask.setCollectionOverdueFee(
                            xCollectionTask.getCollectionOverdueFee() + xRecordLoan.getOverdueInterest());
                        xCollectionTask.setCollectionRepayStatus(CollectionRepayStatusEnum.FINISHED_REPAY.getStatus());
                        xCollectionTask.setCollectionStatus(CollectionStatusEnum.FINISH_COLLECTION.getStatus());
                    }
                    logger.info("{}: 还清全部费用", xRecordRepayment.getOrderId());
                } else {
                    // 还有逾期费用没还, 先还利息, 再还滞纳金
                    if (xRecordLoan.getOverdueInterest() >= diff) {
                        xRecordLoan.setOverdueInterest(xRecordLoan.getOverdueInterest() - diff);

                        if (null != xCollectionTask) {
                            // 更新催收任务中的追回逾期利息
                            xCollectionTask.setCollectionOverdueFee(xCollectionTask.getCollectionOverdueFee() + diff);
                            xCollectionTask
                                .setCollectionRepayStatus(CollectionRepayStatusEnum.UNDER_REPAYING.getStatus());
                        }
                    } else {
                        xRecordLoan.setOverdueInterest(0L);
                        xRecordLoan
                            .setOverdueFee(xRecordLoan.getOverdueFee() + xRecordLoan.getOverdueInterest() - diff);

                        if (null != xCollectionTask) {
                            // 更新催收任务中的追回逾期利息
                            xCollectionTask.setCollectionOverdueFee(
                                xCollectionTask.getCollectionOverdueFee() + xRecordLoan.getOverdueInterest());
                            xCollectionTask
                                .setCollectionRepayStatus(CollectionRepayStatusEnum.UNDER_REPAYING.getStatus());
                        }
                    }
                    xRecordLoan.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_DOING);
                    logger.info("{}: 部分逾期费用未还清:{}", xRecordRepayment.getOrderId(), xRecordLoan.getOverdueFee());
                }
            }

            logger.info("还款状态更新完毕-----------------------");
            xUserInfo.setUpdateTime(now);
            // 最后一次还款时间
            xRecordLoan.setLastRepaymentTime(now);
            logger.info("更新最后一次还款时间------------------------");
            xRecordLoan.setUpdateTime(now);
            if (xUserInfo.getCreditLine() < xUserInfo.getCanborrowAmount()) {
                xUserInfo.setCanborrowAmount(xUserInfo.getCanborrowAmount());
            }
            xUserInfoDao.updateCanborrowAmount(xUserInfo.getCanborrowAmount(), xRecordLoan.getUserGid());
            xRecordLoanDao.update(xRecordLoan);

            // TODO 发送还款成功的通知
            logger.info("开始发送还款成功通知------------------------");
            xFirebaseNoticeService.sendHadRepayNotice(xRecordLoan, xRecordRepayment, xUserInfo);
            logger.info("结束发送还款成功通知------------------------");

            // 更新催收任务信息
            if (null != xCollectionTask) {
                if (xCollectionTask.getCollectorId() != null) {
                    xCollectionTask.setUpdateTime(new Date());
                    xCollectionTaskDao.update(xCollectionTask);

                    // 如果未指派钱就还款
                } else {
                    xCollectionTask.setDueCollectionAmount(
                        xCollectionTask.getDueCollectionAmount() - xCollectionTask.getCollectionAmount());
                    xCollectionTask.setCollectionAmount(0L);
                    xCollectionTask.setCollectionOverdueFee(0L);
                    xCollectionTask.setUpdateTime(new Date());
                }
            }
            // 实际扣款时间
            xRecordRepayment.setEndTime(now);

        } else {
            // 还款失败原因
            xRecordRepayment.setFailReason("baokim调用接口失败，还款失败");
            // 还款状态
            xRecordRepayment.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_FAIL);
        }

        if (xRecordRepaymentDao.save(xRecordRepayment) == 1
            && BaokimResponseStatus.SUCCESS.getCode().equals(responseCode)) {
            // 还款提升授信額度
            threadPoolService.execute(() -> {
                Map<String, Object> params = new HashMap<>();
                params.put("userGid", xRecordLoan.getUserGid());
                restTemplate.postForObject(repayment_success_post_handle_url, null, Long.class, params);
            });
        }
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
