package com.ivay.ivay_repository.dao.master;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.ivay.ivay_repository.model.XUserRisk;

@Mapper
public interface XUserRiskDao {
	// 实时查询社交类app个数
    @Select("select IFNULL((select app_num from x_user_app_num where update_date=#{updateDate} and user_gid=#{userGid}),0) ")
    Integer queryAppNum(@Param("userGid")String userGid,@Param("updateDate")String updateDate);
    
    // 查询14天内社交类app的最大个数
    @Select("select IFNULL((SELECT	max(app_num) FROM (SELECT app_num FROM x_user_app_num WHERE DATEDIFF(date_format(now(), '%Y-%m-%d'),update_date) <= #{updateDate} AND user_gid = #{userGid}) t),0)")
    Integer queryMaxAppNum(@Param("userGid")String userGid,@Param("updateDate")String updateDate);
    
    @Select("select count(1) from x_user_risk where user_gid=#{userGid}")
    Integer findUser(String userGid);
    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO x_user_risk(user_gid, longitude, latitude, create_time, update_time, mac_address, phone_brand, traffic_way) VALUES (#{userGid}, #{longitude}, #{latitude}, #{createTime}, #{updateTime}, #{macAddress}, #{phoneBrand}, #{trafficWay})")
    Integer save(XUserRisk xUserRisk);
    
    Integer updateGpsInfo(XUserRisk xUserRisk);
    
    Integer updateOthers(XUserRisk xUserRisk);
    
    //查询gps信息
    @Select("select * from  x_user_risk where user_gid=#{userGid}")
    XUserRisk getGps(String userGid);
}

