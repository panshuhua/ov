package com.ivay.ivay_manage.controller;

import com.google.common.collect.Maps;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.dto.Response;
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
 * @author sx
 */
@Api(tags = "系统角色")
@RestController
@RequestMapping("manage/role")
public class SysRoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleDao roleDao;

    @GetMapping("list")
    @ApiOperation("查看所有可见角色列表")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public Response<List<SysRole>> listRoles() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("role", roleService.getLoginAdminRole());
        Response<List<SysRole>> response = new Response<>();
        response.setBo(roleDao.listVisible(map, null, null));
        return response;
    }

    @GetMapping("all")
    @ApiOperation("所有角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public Response<List<SysRole>> roles() {
        Response<List<SysRole>> response = new Response<>();
        response.setBo(roleDao.listVisible(null, null, null));
        return response;
    }

    @LogAnnotation
    @PostMapping("save")
    @ApiOperation("保存角色")
    @PreAuthorize("hasAuthority('sys:role:add')")
    public void saveRole(@RequestBody SysRolePermission sysRolePermission) {
        // 新增角色时，id为空，否则为编辑角色
        roleService.saveRole(sysRolePermission);
    }

    @GetMapping("get")
    @ApiOperation("根据id获取角色")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public Response<SysRole> get(@RequestParam Long id) {
        Response<SysRole> response = new Response<>();
        response.setBo(roleDao.getById(id));
        return response;
    }

    @GetMapping("ownList")
    @ApiOperation("根据用户id获取拥有的角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public Response<List<SysRole>> roles(@RequestParam Long userId) {
        Response<List<SysRole>> response = new Response<>();
        response.setBo(roleDao.listByUserId(userId));
        return response;
    }

    @LogAnnotation
    @DeleteMapping("delete")
    @ApiOperation("删除角色")
    @PreAuthorize("hasAuthority('sys:role:del')")
    public void delete(@RequestParam Long id) {
        roleService.deleteRole(id);
    }

    @GetMapping("getCollectRole")
    @ApiOperation("根据用户id获取催收角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public Response<String> getCollectRole(@RequestParam Long userId) {
        Response<String> response = new Response<>();
        response.setBo(roleService.getLoginUserCollectRole());
        return response;
    }
}
