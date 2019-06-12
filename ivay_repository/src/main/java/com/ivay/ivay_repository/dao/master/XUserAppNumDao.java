package com.ivay.ivay_repository.dao.master;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.ivay.ivay_repository.model.XUserAppNum;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface XUserAppNumDao {
	
	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Select("insert into x_user_app_num(user_gid,app_num,update_date) VALUES (#{userGid}, #{appNum}, #{updateDate})")
    Integer saveAppNum(XUserAppNum xUserAppNum);
    
	@Select("select count(1) from x_user_app_num where user_gid=#{userGid} and update_date=#{updateDate}")
	Integer countAppNum(@Param("userGid")String userGid,@Param("updateDate")String updateDate);
	
}
