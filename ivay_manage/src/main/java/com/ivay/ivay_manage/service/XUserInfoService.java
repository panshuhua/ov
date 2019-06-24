package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.dto.XAuditDetail;


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
     * 对待授信用户进行人工审核
     *
     * @param userGid
     * @param flag       0 审核拒绝 1 审核通过
     * @param refuseCode 审核拒绝时，必须传入refuseCode
     * @param refuseDemo
     * @param type       审核类型 0人工 1自动
     * @return 1审核通过 0 审核拒绝 -1数据库异常
     */
    int auditUpdate(String userGid, int flag, String refuseCode, String refuseDemo, String type);

    /**
     * 对待授信用户进行自动审核或分配审计员
     *
     * @param userGid
     * @return
     */
    boolean autoAudit(String userGid);

    /**
     * 获取与某用户同名得所有用户
     *
     * @param request
     * @return
     */
    PageTableResponse listSameName(PageTableRequest request);

    /**
     * 查看逾期用户
     *
     * @param limit
     * @param num
     * @param type
     * @return
     */
    PageTableResponse overDueUsers(int limit, int num, String type);

    /**
     * 查看逾期借款信息
     *
     * @param limit
     * @param num
     * @param userGid
     * @param type
     * @return
     */
    PageTableResponse overDueLoan(int limit, int num, String userGid, String type);
}

