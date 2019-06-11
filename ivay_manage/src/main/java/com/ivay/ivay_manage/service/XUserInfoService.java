package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XAuditDetail;
import com.ivay.ivay_repository.model.XLoanQualification;

public interface XUserInfoService {
    /**
     * 获取审核记录列表
     *
     * @param request
     * @return
     */
    PageTableResponse auditList(PageTableRequest request);

    /**
     * 被审核人详细信息
     *
     * @param userGid
     * @return
     */
    XAuditDetail auditDetail(String userGid);

    /**
     * 提交审核结果
     *
     * @param userGid
     * @param flag
     * @param refuseCode
     * @param refuseDemo
     * @param type       审核类型 0人工 1自动
     * @return
     */
    int auditUpdate(String userGid, int flag, String refuseCode, String refuseDemo, String type);

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
     * @param flag    0 贷前策略 1 贷中策略
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
     * 对提交提交用户进行自动审核处理
     *
     * @param userGid
     * @return
     */
    boolean autoAudit(String userGid);

}

