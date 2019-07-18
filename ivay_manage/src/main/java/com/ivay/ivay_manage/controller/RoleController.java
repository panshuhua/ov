package com.ivay.ivay_manage.controller;

import com.google.common.collect.Maps;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
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
@Api(tags = "角色")
@RestController
@RequestMapping("manage/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleDao roleDao;

    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "保存角色")
    @PreAuthorize("hasAuthority('sys:role:add')")
    public void saveRole(@RequestBody SysRolePermission roleDto) {
        roleService.saveRole(roleDto);
    }

    @GetMapping
    @ApiOperation(value = "角色列表")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public PageTableResponse listRoles(PageTableRequest request) {
        return new PageTableHandler(
                a -> roleDao.count(a.getParams()),
                a -> roleDao.listVisible(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取角色")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public SysRole get(@PathVariable Long id) {
        return roleDao.getById(id);
    }

    @GetMapping("/all")
    @ApiOperation(value = "所有角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public List<SysRole> roles() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("role", roleService.getLoginAdminRole());
        return roleDao.listVisible(map, null, null);
    }

    @GetMapping(params = "userId")
    @ApiOperation(value = "根据用户id获取拥有的角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public List<SysRole> roles(Long userId) {
        return roleDao.listByUserId(userId);
    }

    @LogAnnotation
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除角色")
    @PreAuthorize("hasAuthority('sys:role:del')")
    public void delete(@PathVariable Long id) {
        roleService.deleteRole(id);
    }
}
