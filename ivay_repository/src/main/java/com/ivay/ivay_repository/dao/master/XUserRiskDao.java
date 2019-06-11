package com.ivay.ivay_repository.dao.master;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface XUserRiskDao {
	// 实时查询社交类app个数
    @Select("select IFNULL((select app_num from x_user_app_num where update_date=#{updateDate} and user_gid=#{userGid}),0) ")
    Integer queryAppNum(@Param("userGid")String userGid,@Param("updateDate")String updateDate);
    
    // 查询14天内社交类app的最大个数
    @Select("select IFNULL((SELECT	max(app_num) FROM (SELECT app_num FROM x_user_app_num WHERE DATEDIFF(date_format(now(), '%Y-%m-%d'),update_date) <= #{updateDate} AND user_gid = #{userGid}) t),0)")
    Integer queryMaxAppNum(@Param("userGid")String userGid,@Param("updateDate")String updateDate);
}
