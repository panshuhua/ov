package com.ivay.ivay_manage.dto;


import com.ivay.ivay_repository.model.SysRole;

import java.util.List;

public class SysRolePermission extends SysRole {

    private static final long serialVersionUID = 4218495592167610193L;

    private List<Long> permissionIds;

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
