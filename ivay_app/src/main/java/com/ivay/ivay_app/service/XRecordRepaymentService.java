package com.ivay.ivay_app.service;

import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XRecordRepayment;
import com.ivay.ivay_repository.model.XVirtualAccount;
import com.ivay.ivay_common.table.PageTableResponse;

public interface XRecordRepaymentService {
    /**
     * 查询还款记录
     *
     * @param repaymentGid
     * @param userGid
     * @return
     */
    XRecordRepayment getByGid(String repaymentGid, String userGid);

    /**
     * 还款列表
     *
     * @param limit
     * @param num
     * @param userGid
     * @return
     */
    PageTableResponse list(int limit, int num, String userGid);

    /**
     * 还款
     *
     * @param orderGid        借款gid
     * @param userGid         用户gid
     * @param bankShortName   银行名简称
     * @param repaymentAmount 还款金额
     * @param deductType      还款类型 0:银行卡
     * @return
     */
    XVirtualAccount repaymentMoney(String orderGid, String userGid, String bankShortName, long repaymentAmount, Integer deductType);

    /**
     * 调用银行还款接口
     *
     * @param xRecordLoan
     * @param xRecordRepayment
     */
    void repayMoneyToBank(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment, String responseCode);

    /**
     * 还款接口返回后，更新借款和还款表
     *
     * @param xRecordLoan
     * @param xRecordRepayment
     */
    void confirmRepayment(XRecordLoan xRecordLoan, XRecordRepayment xRecordRepayment, String responseCode);
}
