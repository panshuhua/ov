package com.ivay.ivay_manage.service;

import com.ivay.ivay_manage.dto.RoleDto;

public interface RoleService {

	void saveRole(RoleDto roleDto);

	void deleteRole(Long id);
}
