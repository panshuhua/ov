package com.ivay.ivay_manage.service;


import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XAuditUser;

import java.util.List;

public interface XAuditUserService {
    XAuditUser getById(Long id);

    /**
     * 获取某审计员可审计名单
     *
     * @param sysUserId
     * @return
     */
    List<XAuditUser> getBySysUserId(String sysUserId);

    /**
     * 删除某个审计员
     *
     * @param ids
     * @return
     */
    int delete(String ids);

    /**
     * 为某个用户分配一个审计员,不设置审计员则随机分配
     *
     * @param auditId
     * @param userGid
     * @return
     */
    XAuditUser update(String auditId, String userGid);

    XAuditUser save(XAuditUser xAuditUser);

    /**
     * 获得所有的审计员
     *
     * @param request
     * @return
     */
    PageTableResponse list(PageTableRequest request);
}
