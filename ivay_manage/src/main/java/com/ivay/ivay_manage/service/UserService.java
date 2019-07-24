package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.dto.SysRoleUser;
import com.ivay.ivay_repository.dto.UserName;
import com.ivay.ivay_repository.model.SysUser;

import java.util.List;

public interface UserService {
    /**
     * 新增用户及其角色
     *
     * @param sysRoleUser
     * @return
     */
    SysUser addUser(SysRoleUser sysRoleUser);

    /**
     * 更新用户基本信息和角色
     *
     * @param sysRoleUser
     * @return
     */
    SysUser updateUserAndRole(SysRoleUser sysRoleUser);

    SysUser getUserByName(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    /**
     * @Description 获取用户名列表
     * @Author Ryan
     * @Param []
     * @Return java.util.List<com.ivay.ivay_repository.dto.UserName>
     * @Date 2019/7/10 20:37
     */
    List<UserName> getCollectUserNames();

    /**
     * @Description 查询销售员
     * @Author Ryan
     * @Param []
     * @Return java.util.List<com.ivay.ivay_repository.dto.UserName>
     * @Date 2019/7/24 10:08
     */
    List<UserName> getSalesmanNames();
}
