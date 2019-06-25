package com.ivay.ivay_manage.service;

import com.ivay.ivay_manage.dto.RoleDto;

public interface RoleService {

    void saveRole(RoleDto roleDto);

    void deleteRole(Long id);

    /**
     * 获取当前用户的审计角色
     *
     * @return
     */
    String getLoginUserAuditRole();
}
