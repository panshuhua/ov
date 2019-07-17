package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.SysRole;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleDao {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into sys_role(name, description, createTime, updateTime)" +
            "values(#{name}, #{description}, now(),now())")
    int save(SysRole sysRole);

    int count(@Param("params") Map<String, Object> params);

    List<SysRole> listVisible(@Param("params") Map<String, Object> params,
                              @Param("offset") Integer offset,
                              @Param("limit") Integer limit);

    @Select("select * from sys_role t where t.id = #{id}")
    SysRole getById(Long id);

    @Select("select * from sys_role t where t.name = #{name}")
    SysRole getRoleByName(String name);

    @Update("update sys_role t set t.name = #{name}, t.description = #{description}, updateTime = now() where t.id = #{id}")
    int update(SysRole sysRole);

    @Select("select * from sys_role r" +
            " inner join sys_role_user ru on r.id = ru.roleId" +
            " where ru.userId = #{userId}")
    List<SysRole> listByUserId(Long userId);

    @Delete("delete from sys_role_permission where roleId = #{roleId}")
    int deleteRolePermission(Long roleId);

    int saveRolePermission(@Param("roleId") Long roleId,
                           @Param("permissionIds") List<Long> permissionIds);

    @Delete("delete from sys_role where id = #{id}")
    int delete(Long id);

    @Delete("delete from sys_role_user where roleId = #{roleId}")
    int deleteRoleUser(Long roleId);

}
