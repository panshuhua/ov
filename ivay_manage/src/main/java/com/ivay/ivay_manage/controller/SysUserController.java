package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.dto.SysRoleUser;
import com.ivay.ivay_manage.service.RoleService;
import com.ivay.ivay_manage.service.UserService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.UserDao;
import com.ivay.ivay_repository.model.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    private RoleService roleService;

    @GetMapping("current")
    @ApiOperation("当前登录用户的基本信息")
    public Response<SysUser> currentUser() {
        Response<SysUser> response = new Response<>();
        response.setBo(UserUtil.getLoginUser());
        return response;
    }

    @GetMapping("get")
    @ApiOperation("根据用户id获取用户基本信息")
    @PreAuthorize("hasAuthority('sys:user:query')")
    public Response<SysUser> user(@RequestParam Long id) {
        Response<SysUser> response = new Response<>();
        response.setBo(userDao.getById(id));
        return response;
    }

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
    public Response<SysUser> updateUser(@RequestBody SysRoleUser sysRoleUser) {
        Response<SysUser> response = new Response<>();
        response.setBo(userService.updateUserAndRole(sysRoleUser));
        return response;
    }

    @LogAnnotation
    @PutMapping("headImg")
    @ApiOperation("修改用户头像")
    public void updateHeadImgUrl(@RequestParam String headImgUrl) {
        SysUser user = UserUtil.getLoginUser();
        if (user == null) {
            throw new AccessDeniedException("请登录");
        }
        SysRoleUser sysRoleUser = new SysRoleUser();
        BeanUtils.copyProperties(user, sysRoleUser);
        sysRoleUser.setHeadImgUrl(headImgUrl);

        userDao.update(sysRoleUser);
        logger.debug("{}修改了头像", user.getUsername());
    }

    @LogAnnotation
    @PutMapping("changePassword")
    @ApiOperation("修改密码")
    @PreAuthorize("hasAuthority('sys:user:password')")
    public void changePassword(@RequestParam String username,
                               @RequestParam String oldPassword,
                               @RequestParam String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
    }

    @PostMapping("listUsers")
    @ApiOperation("用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数, 0不分页", dataType = "Long", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "num", value = "页数", dataType = "Long", paramType = "query", defaultValue = "1")
    })
    @PreAuthorize("hasAuthority('sys:user:query')")
    public Response<PageTableResponse> listUsers(@RequestParam(required = false, defaultValue = "0") int limit,
                                                 @RequestParam(required = false, defaultValue = "1") int num,
                                                 @RequestParam String role,
                                                 @RequestBody(required = false) SysUser sysUser) {
        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);
        if (sysUser != null) {
            request.getParams().put("username", sysUser.getUsername());
            request.getParams().put("nickname", sysUser.getNickname());
            request.getParams().put("status", sysUser.getStatus());
        }
        // 设置角色
        request.getParams().put("role", roleService.getLoginAdminRole());
        Response<PageTableResponse> response = new Response<>();
        response.setBo(new PageTableHandler(
                        a -> userDao.count(a.getParams()),
                        a -> userDao.list(a.getParams(), a.getOffset(), a.getLimit())
                ).handle(request)
        );
        return response;
    }
}
