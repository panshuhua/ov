package com.ivay.ivay_app.dto;

import com.ivay.ivay_repository.model.SysUser;

import java.util.List;

public class UserDto extends SysUser {

    private static final long serialVersionUID = -184009306207076712L;

    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

}
