package com.ivay.ivay_manage.controller;

import com.google.common.collect.Maps;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.dto.LoginUser;
import com.ivay.ivay_manage.dto.RoleDto;
import com.ivay.ivay_manage.service.RoleService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.RoleDao;
import com.ivay.ivay_repository.model.Role;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public void saveRole(@RequestBody RoleDto roleDto) {
        roleService.saveRole(roleDto);
    }

    @GetMapping
    @ApiOperation(value = "角色列表")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public PageTableResponse listRoles(PageTableRequest request) {
        return new PageTableHandler(
                a -> roleDao.count(a.getParams()),
                a -> roleDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取角色")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public Role get(@PathVariable Long id) {
        return roleDao.getById(id);
    }

    @GetMapping("/all")
    @ApiOperation(value = "所有角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public List<Role> roles() {
        LoginUser loginUser = UserUtil.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("not login.");
        }
        List<Role> roles = roleDao.listByUserId(loginUser.getId());
        if (roles.size() == 0) {
            throw new BusinessException("not find role.");
        }
        // admin 能查看管理所有角色
        // ovayAdmin 不能查看admin，能管理和查看所有ovayAdmin和ovayAudit角色
        String role = SysVariable.ROLE_OVAY_AUDIT;
        for (Role r : roles) {
            if (SysVariable.ROLE_ADMIN.equals(r.getName())) {
                role = SysVariable.ROLE_ADMIN;
                break;
            }
            if (SysVariable.ROLE_OVAY_ADMIN.equals(r.getName())) {
                role = SysVariable.ROLE_OVAY_ADMIN;
            }
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("role", role);
        return roleDao.list(map, null, null);
    }

    @GetMapping(params = "userId")
    @ApiOperation(value = "根据用户id获取拥有的角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public List<Role> roles(Long userId) {
        return roleDao.listByUserId(userId);
    }

    @LogAnnotation
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除角色")
    @PreAuthorize("hasAuthority('sys:role:del')")
    public void delete(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList();
        list.add("phthon");
        list.add("java");
        list.add("rest");


        for (String str : list) {
            if (str.equals("phthon")) {
                System.out.println("1");
                break;
            }
            if (str.equals("java")) {
                System.out.println("2");
            }
        }
    }
}
