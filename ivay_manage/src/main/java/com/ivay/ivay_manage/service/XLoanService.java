package com.ivay.ivay_manage.service;

import com.ivay.ivay_repository.dto.XLoanQualification;

public interface XLoanService {
    /**
     * 获取贷前策略实体
     *
     * @param userGid
     * @param flag    0 贷前策略 1 贷中策略
     * @return
     */
    XLoanQualification getAuditQualificationObj(String userGid, int flag);

    /**
     * 获得某人的风控审核结果，空字符串表示通过审核
     *
     * @param userGid
     * @param flag    0 授信策略 1 借款策略
     * @return 返回未通过审核的理由
     */
    String queryRiskQualificationDemo(String userGid, int flag);

    /**
     * 获取贷中策略实体
     *
     * @param userGid
     * @return
     */
    XLoanQualification getLoanQualificationObj(XLoanQualification xLoanQualification, String userGid);

    /**
     * 初始化個人借貸信息，包括借款利率、可借貸額度等
     *
     * @param userGid
     */
    void initLoanRateAndCreditLimit(String userGid);

    /**
     * 保存用户的借款利率
     *
     * @param userGid
     */
    int saveLoanRate(String userGid);

    /**
     * 还款成功的后置处理: 包括提额、增加白名单
     *
     * @param userGid
     */
    long repaymentSuccessPostHandle(String userGid);

    /**
     * 獲得某人的授信額度，如果滿足提額條件則提額
     *
     * @param userGid
     */
    long refreshCreditLimit(String userGid);
}
