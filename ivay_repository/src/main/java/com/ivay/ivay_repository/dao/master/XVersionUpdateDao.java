package com.ivay.ivay_repository.dao.master;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.ivay.ivay_repository.model.XVersionUpdate;

@Mapper
public interface XVersionUpdateDao {
	
	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into x_version_update(version_number,version_content,need_update,create_time,update_time,enable_flag) " +
			"VALUES ( #{versionNumber}, #{versionContent}, #{needUpdate},now(),now(),'Y')")
    int save(XVersionUpdate xVersionUpdate);
	
	@Select("SELECT *, ABS(NOW() - create_time) AS diffTime FROM x_version_update ORDER BY diffTime ASC LIMIT 0,1")
	XVersionUpdate findUpdate();
	
}
