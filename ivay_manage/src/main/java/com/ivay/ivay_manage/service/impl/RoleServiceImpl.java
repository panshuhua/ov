package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.dto.LoginUser;
import com.ivay.ivay_manage.dto.SysRolePermission;
import com.ivay.ivay_manage.service.RoleService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.RoleDao;
import com.ivay.ivay_repository.model.SysRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private RoleDao roleDao;

    @Override
    @Transactional
    public void saveRole(SysRolePermission sysRolePermission) {
        SysRole sysRole = sysRolePermission;
        List<Long> permissionIds = sysRolePermission.getPermissionIds();
        // todo 权限为0代表什么
        permissionIds.remove(0L);

        if (sysRole.getId() != null) {
            // 修改角色
            updateRole(sysRole, permissionIds);
        } else {
            // 新增角色
            insertRole(sysRole, permissionIds);
        }
    }

    private void insertRole(SysRole sysRole, List<Long> permissionIds) {
        SysRole r = roleDao.getRoleByName(sysRole.getName());
        if (r != null) {
            throw new IllegalArgumentException(sysRole.getName() + "已存在");
        }
        // 新增角色
        roleDao.save(sysRole);
        if (!CollectionUtils.isEmpty(permissionIds)) {
            // 新增角色对应的权限
            roleDao.saveRolePermission(sysRole.getId(), permissionIds);
        }
        logger.debug("新增角色{}", sysRole.getName());
    }

    private void updateRole(SysRole sysRole, List<Long> permissionIds) {
        SysRole r = roleDao.getRoleByName(sysRole.getName());
        if (r != null && !r.getId().equals(sysRole.getId())) {
            throw new IllegalArgumentException(sysRole.getName() + "已存在");
        }

        // 修改角色
        roleDao.update(sysRole);

        // 删除旧权限
        roleDao.deleteRolePermission(sysRole.getId());
        if (!CollectionUtils.isEmpty(permissionIds)) {
            // 新增权限
            roleDao.saveRolePermission(sysRole.getId(), permissionIds);
        }
        logger.debug("修改角色{}", sysRole.getName());
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        roleDao.deleteRolePermission(id);
        roleDao.deleteRoleUser(id);
        roleDao.delete(id);

        logger.debug("删除角色id:{}", id);
    }

    /**
     * 获取当前用户的审计角色
     *
     * @return 审计管理员、审计员
     */
    @Override
    public String getLoginUserAuditRole() {
        LoginUser loginUser = UserUtil.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        List<SysRole> sysRoles = roleDao.listByUserId(loginUser.getId());
        if (sysRoles.size() == 0) {
            return null;
        }
        // ovayAdmin 能管理和查看所有ovayAdmin和ovayAudit角色
        String role = SysVariable.ROLE_OVAY_AUDIT;
        for (SysRole r : sysRoles) {
            if (SysVariable.ROLE_OVAY_ADMIN.equals(r.getName())) {
                role = SysVariable.ROLE_OVAY_ADMIN;
                break;
            }
        }
        return role;
    }

    /**
     * 获取当前用户的催收角色
     *
     * @return 催收管理员、催收员
     */
    @Override
    public String getLoginUserCollectRole() {
        LoginUser loginUser = UserUtil.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        List<SysRole> sysRoles = roleDao.listByUserId(loginUser.getId());
        if (sysRoles.size() == 0) {
            return null;
        }
        String role = SysVariable.ROLE_COLLECTION_PERSON;
        for (SysRole r : sysRoles) {
            if (SysVariable.ROLE_COLLECTION_ADMIN.equals(r.getName())) {
                role = SysVariable.ROLE_COLLECTION_ADMIN;
                break;
            }
        }
        return role;
    }

    /**
     * 获取当前管理员权限
     *
     * @return
     */
    @Override
    public String getLoginAdminRole() {
        LoginUser loginUser = UserUtil.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        List<SysRole> sysRoles = roleDao.listByUserId(loginUser.getId());
        if (sysRoles.size() == 0) {
            return null;
        }
        String role = SysVariable.ROLE_COLLECTION_ADMIN;
        for (SysRole r : sysRoles) {
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
