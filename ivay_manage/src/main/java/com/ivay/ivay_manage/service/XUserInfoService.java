package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.dto.XAuditDetail;
import com.ivay.ivay_manage.dto.XLoanQualification;

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
     * 提交审核
     *
     * @param userGid
     * @return
     */
    int auditUpdate(String userGid, int flag);

    /**
     * 获取贷前策略实体
     *
     * @param userGid
     * @param flag    0 贷前策略 1 贷中策略
     * @return
     */
    XLoanQualification getAuditQualificationObj(String userGid, int flag);

    /**
     * 判断是否有资格
     *
     * @param userGid
     * @param flag    0 贷前策略 1 贷中策略
     * @return
     */
    boolean queryAuditQualification(String userGid, int flag);

    /**
     * 获取贷中策略实体
     *
     * @param userGid
     * @return
     */
    XLoanQualification getLoanQualificationObj(XLoanQualification xLoanQualification, String userGid);

}

