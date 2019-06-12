package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.dto.LoginUser;
import com.ivay.ivay_manage.dto.RoleDto;
import com.ivay.ivay_manage.service.RoleService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.RoleDao;
import com.ivay.ivay_repository.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private RoleDao roleDao;

    @Override
    @Transactional
    public void saveRole(RoleDto roleDto) {
        Role role = roleDto;
        List<Long> permissionIds = roleDto.getPermissionIds();
        permissionIds.remove(0L);

        if (role.getId() != null) {// 修改
            updateRole(role, permissionIds);
        } else {// 新增
            saveRole(role, permissionIds);
        }
    }

    private void saveRole(Role role, List<Long> permissionIds) {
        Role r = roleDao.getRole(role.getName());
        if (r != null) {
            throw new IllegalArgumentException(role.getName() + "已存在");
        }

        roleDao.save(role);
        if (!CollectionUtils.isEmpty(permissionIds)) {
            roleDao.saveRolePermission(role.getId(), permissionIds);
        }
        log.debug("新增角色{}", role.getName());
    }

    private void updateRole(Role role, List<Long> permissionIds) {
        Role r = roleDao.getRole(role.getName());
        if (r != null && !r.getId().equals(role.getId())) {
            throw new IllegalArgumentException(role.getName() + "已存在");
        }

        roleDao.update(role);

        roleDao.deleteRolePermission(role.getId());
        if (!CollectionUtils.isEmpty(permissionIds)) {
            roleDao.saveRolePermission(role.getId(), permissionIds);
        }
        log.debug("修改角色{}", role.getName());
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        roleDao.deleteRolePermission(id);
        roleDao.deleteRoleUser(id);
        roleDao.delete(id);

        log.debug("删除角色id:{}", id);
    }

    /**
     * 获取当前用户的最高角色，用户审计
     *
     * @return
     */
    @Override
    public String getLoginUserAuditRole() {
        LoginUser loginUser = UserUtil.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        List<Role> roles = roleDao.listByUserId(loginUser.getId());
        if (roles.size() == 0) {
            return null;
        }
        // admin 能查看管理所有角色
        // ovayAdmin 不能查看admin，能管理和查看所有ovayAdmin和ovayAudit角色
        String role = SysVariable.ROLE_OVAY_AUDIT;
        for (Role r : roles) {
            if (SysVariable.ROLE_ADMIN.equals(r.getName())) {
                role = SysVariable.ROLE_ADMIN;
                break;
            }
            if (SysVariable.ROLE_OVAY_ADMIN.equals(r.getName())) {
                role = SysVariable.ROLE_OVAY_ADMIN;
            }
        }
        return role;
    }
}
