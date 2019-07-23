package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.dto.SysRoleUser;
import com.ivay.ivay_manage.service.RoleService;
import com.ivay.ivay_manage.service.UserService;
import com.ivay.ivay_manage.service.XAuditService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.RoleDao;
import com.ivay.ivay_repository.dao.master.UserDao;
import com.ivay.ivay_repository.model.SysRole;
import com.ivay.ivay_repository.model.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户相关接口
 *
 * @author sx
 */
@Api(tags = "系统用户")
@RestController
@RequestMapping("manage/user")
public class SysUserController {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RoleService roleService;

    @Autowired
    private XAuditService xAuditService;

    @LogAnnotation
    @PostMapping("add")
    @ApiOperation("添加用户")
    @PreAuthorize("hasAuthority('sys:user:add')")
    public Response<SysUser> saveUser(@RequestBody SysRoleUser sysRoleUser) {
        Response<SysUser> response = new Response<>();
        response.setBo(userService.addUser(sysRoleUser));
        return response;
    }

    @LogAnnotation
    @PutMapping("update")
    @ApiOperation("修改用户")
    @PreAuthorize("hasAuthority('sys:user:add')")
    public SysUser updateUser(@RequestBody SysRoleUser sysRoleUser) {
        List<SysRole> sysRoleList = roleDao.listByUserId(sysRoleUser.getId());
        SysUser sysUser = userService.updateUser(sysRoleUser);
        if (isDelOvayAuditRight(sysRoleList, sysRoleUser.getRoleIds())) {
            // 为被当前审计员审核的人员重新分配审计员
            xAuditService.reAssignAudit(null, sysRoleUser.getId().toString());
        }
        return sysUser;
    }

    private boolean isDelOvayAuditRight(List<SysRole> oldSysRoleList, List<Long> newRoleId) {
        SysRole ovayAudit = roleDao.getRoleByName(SysVariable.ROLE_OVAY_AUDIT);
        if (!newRoleId.contains(ovayAudit.getId())) {
            for (SysRole r : oldSysRoleList) {
                if (r.getId().equals(ovayAudit.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @LogAnnotation
    @PutMapping("headImg")
    @ApiOperation("修改头像")
    public void updateHeadImgUrl(@RequestParam String headImgUrl) {
        SysUser user = UserUtil.getLoginUser();
        SysRoleUser sysRoleUser = new SysRoleUser();
        BeanUtils.copyProperties(user, sysRoleUser);
        sysRoleUser.setHeadImgUrl(headImgUrl);

        userService.updateUser(sysRoleUser);
        logger.debug("{}修改了头像", user.getUsername());
    }

    @LogAnnotation
    @PutMapping("changePassword/{username}")
    @ApiOperation("修改密码")
    @PreAuthorize("hasAuthority('sys:user:password')")
    public void changePassword(@PathVariable String username,
                               String oldPassword,
                               String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
    }

    @GetMapping("listUsers")
    @ApiOperation("用户列表")
    @PreAuthorize("hasAuthority('sys:user:query')")
    public PageTableResponse listUsers(PageTableRequest request) {
        // 设置角色
        request.getParams().put("role", roleService.getLoginUserAuditRole());
        return new PageTableHandler(
                a -> userDao.count(a.getParams()),
                a -> userDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @ApiOperation("当前登录用户")
    @GetMapping("current")
    public SysUser currentUser() {
        return UserUtil.getLoginUser();
    }

    @ApiOperation("根据用户id获取用户")
    @GetMapping("get/{id}")
    @PreAuthorize("hasAuthority('sys:user:query')")
    public SysUser user(@PathVariable Long id) {
        return userDao.getById(id);
    }
}
