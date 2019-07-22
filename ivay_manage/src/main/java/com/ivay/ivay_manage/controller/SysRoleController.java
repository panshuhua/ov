package com.ivay.ivay_manage.controller;

import com.google.common.collect.Maps;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.dto.SysRolePermission;
import com.ivay.ivay_manage.service.RoleService;
import com.ivay.ivay_repository.dao.master.RoleDao;
import com.ivay.ivay_repository.model.SysRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色相关接口
 *
 * @author xx
 */
@Api(tags = "系统角色")
@RestController
@RequestMapping("manage/role")
public class SysRoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleDao roleDao;

    @LogAnnotation
    @PostMapping("save")
    @ApiOperation("保存角色")
    @PreAuthorize("hasAuthority('sys:role:add')")
    public void saveRole(@RequestBody SysRolePermission sysRolePermission) {
        // 新增角色时，id为空，否则为编辑角色
        roleService.saveRole(sysRolePermission);
    }

    @GetMapping("list")
    @ApiOperation(value = "角色列表")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public PageTableResponse listRoles() {
        return new PageTableResponse<>(roleDao.listVisible(null, null, null));
    }

    @GetMapping("get/{id}")
    @ApiOperation(value = "根据id获取角色")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public SysRole get(@PathVariable Long id) {
        return roleDao.getById(id);
    }

    @GetMapping("all")
    @ApiOperation(value = "所有角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public List<SysRole> roles() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("role", roleService.getLoginUserAuditRole());
        return roleDao.listVisible(map, null, null);
    }

    @GetMapping("getUserRoles")
    @ApiOperation(value = "根据用户id获取拥有的角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public List<SysRole> roles(Long userId) {
        return roleDao.listByUserId(userId);
    }

    @LogAnnotation
    @DeleteMapping("delete/{id}")
    @ApiOperation(value = "删除角色")
    @PreAuthorize("hasAuthority('sys:role:del')")
    public void delete(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

    @GetMapping("getCollectRole")
    @ApiOperation("根据用户id获取催收角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public Response<String> getCollectRole(Long userId) {
        Response<String> response = new Response<>();
        response.setBo(roleService.getLoginUserCollectRole());
        return response;
    }
}
