package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_manage.dto.SysRoleUser;
import com.ivay.ivay_manage.service.UserService;
import com.ivay.ivay_repository.dao.master.UserDao;
import com.ivay.ivay_repository.dto.UserName;
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
        SysUser user = sysRoleUser;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(SysUser.Status.VALID);
        userDao.insert(user);

        // 添加用户的角色
        saveUserRoles(user.getId(), sysRoleUser.getRoleIds());

        logger.debug("新增用户{}", user.getUsername());
        return user;
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

    @Override
    @Transactional
    public SysUser updateUser(SysRoleUser sysRoleUser) {
        userDao.update(sysRoleUser);
        saveUserRoles(sysRoleUser.getId(), sysRoleUser.getRoleIds());

        return sysRoleUser;
    }

    @Override
    public List<UserName> getUserNames() {
        return userDao.getUserNames();
    }

}
