package com.ivay.ivay_manage.service;

import com.ivay.ivay_repository.model.Permission;

public interface PermissionService {

    void save(Permission permission);

    void update(Permission permission);

    void delete(Long id);
}
