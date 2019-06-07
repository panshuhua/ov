package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.RoleDto;

public interface RoleService {

    void saveRole(RoleDto roleDto);

    void deleteRole(Long id);
}
