package com.ivay.ivay_manage.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_manage.dto.LoginUser;
import com.ivay.ivay_manage.service.PermissionService;
import com.ivay.ivay_manage.service.RoleService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.PermissionDao;
import com.ivay.ivay_repository.model.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限相关接口
 *
 * @author sx
 */
@Api(tags = "系统权限-菜单相关")
@RestController
@RequestMapping("manage/permission")
public class SysPermissionController {
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private PermissionService permissionService;

    @GetMapping("ownList")
    @ApiOperation("获取当前用户可以查看的菜单列表")
    public Response<List<Permission>> permissionsCurrent() {
        LoginUser loginUser = UserUtil.getLoginUser();
        List<Permission> list = loginUser.getPermissions();
        // type=1 菜单  type=2 权限？
        final List<Permission> permissions = list.stream().filter(t -> t.getType().equals(1)).collect(Collectors.toList());

        // parentId=0 一级目录
        List<Permission> firstLevel = permissions.stream().filter(p -> p.getParentId().equals(0L)).collect(Collectors.toList());
        // 设置子菜单
        firstLevel.parallelStream().forEach(p -> setChild(p, permissions));

        Response<List<Permission>> response = new Response<>();
        response.setBo(firstLevel);
        return response;
    }

    /**
     * 设置子菜单，支持多级菜单
     *
     * @param p           当前菜单
     * @param permissions 全部菜单
     */
    private void setChild(Permission p, List<Permission> permissions) {
        // 首先过滤出子菜单
        List<Permission> child = permissions.parallelStream().filter(a -> a.getParentId().equals(p.getId())).collect(Collectors.toList());
        p.setChild(child);
        if (!CollectionUtils.isEmpty(child)) {
            // 然后递归设置子子菜单
            child.parallelStream().forEach(c -> setChild(c, permissions));
        }
    }

    @GetMapping("allList")
    @ApiOperation("查询完整的菜单列表")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public Response<List<Permission>> permissionsList() {
        // 获得所有的菜单
        List<Permission> permissionsAll = permissionDao.listAll();
        List<Permission> list = Lists.newArrayList();
        setPermissionsList(0L, permissionsAll, list);
        Response<List<Permission>> response = new Response<>();
        response.setBo(list);
        return response;
    }

    /**
     * 菜单列表
     *
     * @param pId
     * @param permissionsAll
     * @param list
     */
    private void setPermissionsList(Long pId, List<Permission> permissionsAll, List<Permission> list) {
        for (Permission per : permissionsAll) {
            if (per.getParentId().equals(pId)) {
                list.add(per);
                if (permissionsAll.stream().filter(p -> p.getParentId().equals(per.getId())).findAny() != null) {
                    setPermissionsList(per.getId(), permissionsAll, list);
                }
            }
        }
    }

    @GetMapping("allTree")
    @ApiOperation("查看完整的菜单树, 包括新增查询等")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public Response<JSONArray> permissionsAll() {
        List<Permission> permissionsAll = permissionDao.listAll();
        JSONArray array = new JSONArray();
        setPermissionsTree(0L, permissionsAll, array);

        Response<JSONArray> response = new Response<>();
        response.setBo(array);
        return response;
    }

    /**
     * 菜单树
     *
     * @param pId
     * @param permissionsAll
     * @param array
     */
    private void setPermissionsTree(Long pId, List<Permission> permissionsAll, JSONArray array) {
        for (Permission per : permissionsAll) {
            if (per.getParentId().equals(pId)) {
                String string = JSONObject.toJSONString(per);
                JSONObject parent = (JSONObject) JSONObject.parse(string);
                array.add(parent);

                if (permissionsAll.stream().filter(p -> p.getParentId().equals(per.getId())).findAny() != null) {
                    JSONArray child = new JSONArray();
                    parent.put("child", child);
                    setPermissionsTree(per.getId(), permissionsAll, child);
                }
            }
        }
    }

    @GetMapping("menuList")
    @ApiOperation("下拉菜单列表")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public Response<List<Permission>> parentMenu() {
        Response<List<Permission>> response = new Response<>();
        response.setBo(permissionDao.listParents());
        return response;
    }

    @GetMapping("getByRole")
    @ApiOperation("根据角色id获取权限")
    @PreAuthorize("hasAnyAuthority('sys:menu:query','sys:role:query')")
    public List<Permission> listByRoleId(@RequestParam Long roleId) {
        return permissionDao.listByRoleId(roleId);
    }

    @LogAnnotation
    @PostMapping("add")
    @ApiOperation("添加菜单")
    @PreAuthorize("hasAuthority('sys:menu:add')")
    public void save(@RequestBody Permission permission) {
        permissionDao.save(permission);
    }

    @GetMapping("get")
    @ApiOperation("根据菜单id获取菜单")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public Response<Permission> get(@RequestParam Long id) {
        Response<Permission> response = new Response<>();
        response.setBo(permissionDao.getById(id));
        return response;
    }

    @LogAnnotation
    @PutMapping("update")
    @ApiOperation("修改菜单")
    @PreAuthorize("hasAuthority('sys:menu:add')")
    public void update(@RequestBody Permission permission) {
        permissionService.update(permission);
    }

    /**
     * 校验权限
     *
     * @return
     */
    @GetMapping("checkOwns")
    @ApiOperation("校验当前用户用户的权限")
    public Response<Set<String>> ownsPermission() {
        Response<Set<String>> response = new Response<>();
        List<Permission> permissions = UserUtil.getLoginUser().getPermissions();
        if (CollectionUtils.isEmpty(permissions)) {
            response.setBo(Collections.emptySet());
        } else {
            response.setBo(permissions.parallelStream()
                    .filter(p -> !StringUtils.isEmpty(p.getPermission()))
                    .map(Permission::getPermission)
                    .collect(Collectors.toSet())
            );
        }
        return response;
    }

    @LogAnnotation
    @DeleteMapping("delete")
    @ApiOperation("删除菜单")
    @PreAuthorize("hasAuthority('sys:menu:del')")
    public void delete(@RequestParam Long id) {
        permissionService.delete(id);
    }

    @Autowired
    private RoleService roleService;

    @GetMapping("getLoginAuditRole")
    @ApiOperation("获取当前用户的审计权限")
    public String getLoginUserRole() {
        return roleService.getLoginUserAuditRole();
    }
}
