package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.UserDto;
import com.ivay.ivay_repository.model.SysUser;

public interface UserService {

    SysUser saveUser(UserDto userDto);

    SysUser updateUser(UserDto userDto);

    SysUser getUser(String username);

    void changePassword(String username, String oldPassword, String newPassword);

}
