package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.SysUser;
import com.ivay.ivay_repository.model.XAuditUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XAuditUserDao {
    @Select("select * from x_audit_user t where t.id = #{id}")
    XAuditUser getById(Long id);

    @Select("select * from x_audit_user t where t.user_gid = #{userGid} and t.enable_flag = 'Y'")
    XAuditUser getByUserGid(@Param("userGid") String userGid);

    @Select("select * from x_audit_user t where t.sys_user_id = #{sysUserId} and t.enable_flag = 'Y'")
    List<XAuditUser> getBySysUserId(@Param("sysUserId") String sysUserId);

    @Delete("delete from x_audit_user where id = #{id}")
    int delete(Long id);

    @Delete("delete from x_audit_user where enable_flag='Y'")
    int deleteAll();

    // 批量逻辑删除
    int deleteByBatch(@Param("ids") String[] ids);

    // 批量逻辑删除
    int deleteUser(@Param("ids") String[] ids);

    int update(XAuditUser xAuditUser);

    int reAssignAudit(@Param("acceptId") String acceptId, @Param("handleId") String handleId);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_audit_user(sys_user_id, user_gid, create_time, update_time, enable_flag) values(#{sysUserId}, #{userGid}, #{createTime}, #{updateTime}, #{enableFlag})")
    int save(XAuditUser xAuditUser);

    // 查出某一角色的所有用户信息
    int count(@Param("params") Map<String, Object> params);

    List<SysUser> list(@Param("params") Map<String, Object> params,
                       @Param("offset") Integer offset,
                       @Param("limit") Integer limit);

    /**
     * 查出某一角色的所有用户id
     *
     * @param role
     * @return
     */
    List<String> getSysUserByRole(@Param("role") String role);
}
