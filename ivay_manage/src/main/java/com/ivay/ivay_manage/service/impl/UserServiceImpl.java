package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.dto.SysRoleUser;
import com.ivay.ivay_manage.service.RoleService;
import com.ivay.ivay_manage.service.UserService;
import com.ivay.ivay_manage.service.XAuditService;
import com.ivay.ivay_repository.dao.master.RoleDao;
import com.ivay.ivay_repository.dao.master.UserDao;
import com.ivay.ivay_repository.dto.UserName;
import com.ivay.ivay_repository.model.SysRole;
import com.ivay.ivay_repository.model.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private UserDao userDao;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SysUser addUser(SysRoleUser sysRoleUser) {
        SysUser u = getUserByName(sysRoleUser.getUsername());
        if (u != null) {
            throw new IllegalArgumentException(sysRoleUser.getUsername() + "已存在");
        }
        sysRoleUser.setPassword(passwordEncoder.encode(sysRoleUser.getPassword()));
        sysRoleUser.setStatus(SysUser.Status.VALID);

        // 添加用户
        userDao.insert(sysRoleUser);

        // 添加用户的角色
        saveUserRoles(sysRoleUser.getId(), sysRoleUser.getRoleIds());

        logger.debug("新增用户{}", sysRoleUser.getUsername());
        return sysRoleUser;
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null) {
            userDao.deleteUserRole(userId);
            if (!CollectionUtils.isEmpty(roleIds)) {
                userDao.saveUserRoles(userId, roleIds);
            }
        }
    }

    @Override
    public SysUser getUserByName(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        return userDao.getUserByName(username);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        SysUser u = userDao.getUserByName(username);
        if (u == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, u.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }

        userDao.changePassword(u.getId(), passwordEncoder.encode(newPassword));

        logger.debug("修改{}的密码", username);
    }


    @Autowired
    private RoleDao roleDao;
    @Autowired
    private XAuditService xAuditService;

    /**
     * 更新用户基本信息和角色
     *
     * @param sysRoleUser
     * @return
     */
    @Override
    @Transactional
    public SysUser updateUserAndRole(SysRoleUser sysRoleUser) {
        // 旧用户角色
        List<SysRole> oldRoleList = roleDao.listByUserId(sysRoleUser.getId());

        // 更新用户基本信息
        userDao.update(sysRoleUser);
        // 更新用户角色
        saveUserRoles(sysRoleUser.getId(), sysRoleUser.getRoleIds());

        // 如果删除了审计员角色
        if (isDelOvayAuditRight(oldRoleList, sysRoleUser.getRoleIds())) {
            // 为待审核用户重新分配审计员
            xAuditService.reAssignAudit(null, sysRoleUser.getId().toString());
        }
        // 如果删除了催收员角色
        if (isDelCollectionRight(oldRoleList, sysRoleUser.getRoleIds())) {
            // todo 需要删除催收员信息，或者自动重新指派
            logger.info("删除催收员");
        }
        return sysRoleUser;
    }

    private boolean isDelOvayAuditRight(List<SysRole> oldSysRoleList, List<Long> newRoleId) {
        SysRole ovayAudit = roleDao.getRoleByName(SysVariable.ROLE_OVAY_AUDIT);
        if (!newRoleId.contains(ovayAudit.getId())) {
            for (SysRole r : oldSysRoleList) {
                if (r.getId().equals(ovayAudit.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDelCollectionRight(List<SysRole> oldSysRoleList, List<Long> newRoleId) {
        SysRole collector = roleDao.getRoleByName(SysVariable.ROLE_COLLECTION_PERSON);
        if (!newRoleId.contains(collector.getId())) {
            for (SysRole r : oldSysRoleList) {
                if (r.getId().equals(collector.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Autowired
    private RoleService roleService;

    @Override
    public List<UserName> getCollectUserNames() {
        return userDao.getCollectUserNames(roleService.getLoginUserCollectRole());
    }

    @Override
    public List<UserName> getSalesmanNames() {
        return userDao.getSalesNames(roleService.getLoginUserSalesRole());
    }


}
