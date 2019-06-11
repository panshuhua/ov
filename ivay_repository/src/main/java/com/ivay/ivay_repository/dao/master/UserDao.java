package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserDao {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into sys_user(username, password, nickname, headImgUrl, phone, telephone, email," +
            " birthday, sex, status, createTime, updateTime,enable_flag)" +
            "values(#{username}, #{password}, #{nickname}, #{headImgUrl}, #{phone}, #{telephone}, #{email}," +
            " #{birthday}, #{sex}, #{status}, now(), now(),'Y')")
    int save(SysUser user);

    @Select("select * from sys_user t where t.id = #{id} and t.enable_flag='Y'")
    SysUser getById(Long id);

    @Select("select * from sys_user t where t.username = #{username} and t.enable_flag='Y'")
    SysUser getUser(String username);

    @Update("update sys_user t set t.password = #{password} where t.id = #{id}")
    int changePassword(@Param("id") Long id, @Param("password") String password);

    Integer count(@Param("params") Map<String, Object> params);

    List<SysUser> list(@Param("params") Map<String, Object> params,
                       @Param("offset") Integer offset,
                       @Param("limit") Integer limit);

    @Delete("delete from sys_role_user where userId = #{userId}")
    int deleteUserRole(Long userId);

    int saveUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    int update(SysUser user);
}
