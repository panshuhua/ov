package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XUserRisk;
import org.apache.ibatis.annotations.*;

@Mapper
public interface XUserRiskDao {
    // 实时查询社交类app个数
    @Select("select IFNULL((select app_num from x_user_app_num where update_date=#{updateDate} and user_gid=#{userGid} and enable_flag='Y'),0) ")
    Integer queryAppNum(@Param("userGid") String userGid,
                        @Param("updateDate") String updateDate);

    // 查询14天内社交类app的最大个数
    @Select("select IFNULL((SELECT	max(app_num) FROM (SELECT app_num FROM x_user_app_num WHERE DATEDIFF(date_format(now(), '%Y-%m-%d'),update_date) <= #{updateDate} and user_gid = #{userGid} and enable_flag='Y') t),0)")
    Integer queryMaxAppNum(@Param("userGid") String userGid, @Param("updateDate") String updateDate);

    @Select("select count(1) from x_user_risk where user_gid=#{userGid} and enable_flag='Y'")
    Integer countByUserGid(String userGid);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO x_user_risk (user_gid, longitude, latitude, enable_flag, create_time, update_time, imei, imsi, phone_number, carrier_name, phone_model, uid, wifi_mac_address, system_version, ipv4_address) VALUES (#{userGid}, #{longitude}, #{latitude}, 'Y', now(), now(), #{imei}, #{imsi}, #{phoneNumber}, #{carrierName}, #{phoneModel}, #{uid}, #{wifiMacAddress}, #{systemVersion}, #{ipv4Address})")
    Integer save(XUserRisk xUserRisk);

    Integer updateGpsInfo(XUserRisk xUserRisk);

    Integer updateOthers(XUserRisk xUserRisk);

    // 查询gps信息
    @Select("select * from  x_user_risk where user_gid=#{userGid} and enable_flag='Y'")
    XUserRisk getByUserGid(String userGid);
}
