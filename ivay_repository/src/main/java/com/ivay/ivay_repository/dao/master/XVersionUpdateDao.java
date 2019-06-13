package com.ivay.ivay_repository.dao.master;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import com.ivay.ivay_repository.model.XVersionUpdate;

@Mapper
public interface XVersionUpdateDao {
	
	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into x_version_update(user_gid,version_number,version_content,need_update,create_time,update_time,enable_flag) VALUES (#{userGid}, #{versionNumber}, #{versionContent}, #{needUpdate},now(),now(),'Y')")
    int save(XVersionUpdate xVersionUpdate);
	
}
