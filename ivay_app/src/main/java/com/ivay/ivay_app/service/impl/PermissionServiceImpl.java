package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.service.PermissionService;
import com.ivay.ivay_repository.dao.master.PermissionDao;
import com.ivay.ivay_repository.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public void save(Permission permission) {
        permissionDao.save(permission);

        log.debug("新增菜单{}", permission.getName());
    }

    @Override
    public void update(Permission permission) {
        permissionDao.update(permission);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        permissionDao.deleteRolePermission(id);
        permissionDao.delete(id);
        permissionDao.deleteByParentId(id);

        log.debug("删除菜单id:{}", id);
    }

}
