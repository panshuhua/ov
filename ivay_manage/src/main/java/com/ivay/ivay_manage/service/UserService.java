package com.ivay.ivay_manage.service;

import com.ivay.ivay_manage.dto.SysRoleUser;
import com.ivay.ivay_repository.dto.UserName;
import com.ivay.ivay_repository.model.SysUser;

import java.util.List;

public interface UserService {
    /**
     * 添加用户及其角色
     *
     * @param sysRoleUser
     * @return
     */
    SysUser addUser(SysRoleUser sysRoleUser);

    SysUser updateUser(SysRoleUser sysRoleUser);

    SysUser getUserByName(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    /**
     * @Description 获取用户名列表
     * @Author Ryan
     * @Param []
     * @Return java.util.List<com.ivay.ivay_repository.dto.UserName>
     * @Date 2019/7/10 20:37
     */
    List<UserName> getUserNames();
}
