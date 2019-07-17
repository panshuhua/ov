package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.dto.XOverDueFee;
import com.ivay.ivay_repository.model.XRecordLoan;

import java.math.BigDecimal;
import java.util.List;

public interface XRecordLoanService {
    /**
     * 提交借款申请
     *
     * @param xRecordLoan
     * @param password
     * @return 返回借款失败的理由
     */
    String borrowMoney(XRecordLoan xRecordLoan, String password);

    /**
     * 借款记录
     *
     * @param limit
     * @param num
     * @param userGid
     * @return
     */
    PageTableResponse borrowList(int limit, int num, String userGid);

    /**
     * 查询借款信息
     *
     * @param gid
     * @param userGid
     * @return
     */
    XRecordLoan getRecordLoan(String gid, String userGid);

    /**
     * 逾期费用 = 逾期计息 + 逾期滞纳金，加起来不能超过本金
     *
     * @return
     */
    boolean calcOverDueFee();

    /**
     * 计算总逾期费用
     *
     * @param dueAmount 剩余本金
     * @param day       逾期天数
     * @return
     */
    long calcOverDueFee(long dueAmount, int day, BigDecimal loanRate, int loanPeriod);

    /**
     * 计算逾期一天的滞纳金
     *
     * @param list
     */
    void calcOverDueFeeFirstDay(List<XOverDueFee> list);

    /**
     * 确认放款
     *
     * @param xRecordLoan
     * @param transfersRsp
     * @return
     */
    void confirmLoan(XRecordLoan xRecordLoan, TransfersRsp transfersRsp);

    /**
     * 当前总共借款金额
     *
     * @param userGid
     * @return
     */
    long getSumLoanAmount(String userGid);

    /**
     * 查看借款超时的到账信息
     */
    void timeoutTransferInfo();
}
