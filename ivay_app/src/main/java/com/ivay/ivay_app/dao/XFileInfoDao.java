package com.ivay.ivay_app.dao;

import com.ivay.ivay_app.model.XFileInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XFileInfoDao {

	@Select("select * from file_info t where t.id = #{id}")
    XFileInfo getById(String id);

	@Insert("insert into file_info(id, contentType, size, path, url, type, createTime, updateTime) values(#{id}, #{contentType}, #{size}, #{path}, #{url}, #{type}, now(), now())")
	int save(XFileInfo XFileInfo);

	@Update("update file_info t set t.updateTime = now() where t.id = #{id}")
	int update(XFileInfo XFileInfo);

	@Delete("delete from file_info where id = #{id}")
	int delete(String id);

	int count(@Param("params") Map<String, Object> params);

	List<XFileInfo> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                         @Param("limit") Integer limit);

}
