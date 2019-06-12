package com.ivay.ivay_manage.service;

import com.ivay.ivay_manage.dto.RoleDto;

public interface RoleService {

    void saveRole(RoleDto roleDto);

    void deleteRole(Long id);

    /**
     * 获取当前用户的最高角色，用户审计
     *
     * @return
     */
    String getLoginUserAuditRole();
}
