package com.ivay.ivay_manage.service;

import com.ivay.ivay_manage.dto.SysRolePermission;

public interface RoleService {

    void saveRole(SysRolePermission roleDto);

    void deleteRole(Long id);

    /**
     * 获取当前用户的审计角色
     *
     * @return 审计管理员、审计员
     */
    String getLoginUserAuditRole();

    /**
     * 获取当前用户的催收角色
     *
     * @return 催收管理员、催收员
     */
    String getLoginUserCollectRole();
}
