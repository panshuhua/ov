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
    int deleteAudit(String ids);

    /**
     * 删除被某个审计员审计的用户
     *
     * @param ids
     * @return
     */
    int deleteUser(String ids);

    /**
     * 为某个用户分配一个审计员,不设置审计员则随机分配
     *
     * @param auditId
     * @param userGid
     * @return
     */
    XAuditUser assignAuditForUser(String auditId, String userGid);

    /**
     * 将某审计员的审核人员重新分配
     *
     * @param acceptId
     * @param handleId
     * @return
     */
    boolean reAssignAudit(String acceptId, String handleId);

    XAuditUser save(XAuditUser xAuditUser);

    /**
     * 获得所有的审计员
     *
     * @param request
     * @return
     */
    PageTableResponse listAudit(PageTableRequest request);

    /**
     * 获取某审计员可审计名单
     *
     * @param request
     * @return
     */
    PageTableResponse listUser(PageTableRequest request);
}
