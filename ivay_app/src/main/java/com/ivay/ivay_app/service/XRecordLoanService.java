package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_common.table.PageTableResponse;

public interface XRecordLoanService {
    /**
     * 提交借款申请
     *
     * @param xRecordLoan
     * @param password
     * @return
     */
    XRecordLoan borrowMoney(XRecordLoan xRecordLoan, String password);

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
     * 逾期计息 + 逾期滞纳金，都不能超过本金
     *
     * @return
     */
    boolean calcOverDueFee();

    /**
     * 逾期计息 + 逾期滞纳金，加起来不能超过本金
     *
     * @return
     */
    boolean calcOverDueFee2();

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
}
