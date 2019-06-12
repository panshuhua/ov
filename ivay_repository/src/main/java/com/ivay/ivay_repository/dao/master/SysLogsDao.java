package com.ivay.ivay_repository.dao.master;

import java.util.List;
import java.util.Map;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import com.ivay.ivay_repository.model.SysLogs;

@Mapper
public interface SysLogsDao {

	@Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into sys_logs(user_gid, module, flag, remark, createTime,phone,request_id) values(#{userGid}, #{module}, #{flag}, #{remark}, now(),#{phone},#{requestId})")
    int save(SysLogs sysLogs);

    int count(@Param("params") Map<String, Object> params);

    List<SysLogs> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                       @Param("limit") Integer limit);

    @Delete("delete from sys_logs where createTime <= #{time}")
    int deleteLogs(String time);
}
