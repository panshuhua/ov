package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XUserExtInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XUserExtInfoDao {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_user_ext_info(user_gid, major_relationship, major_name, major_phone, bak_relationship, bak_name, bak_phone," +
            " photo1_url, photo2_url, photo3_url, create_time, update_time, enable_flag)" +
            " values(#{userGid}, #{majorRelationship}, #{majorName}, #{majorPhone}, #{bakRelationship}, #{bakName}, #{bakPhone}," +
            " #{photo1Url}, #{photo2Url}, #{photo3Url}, #{createTime}, #{updateTime}, #{enableFlag})")
    int save(XUserExtInfo xUserExtInfo);

    XUserExtInfo getByGid(@Param("userGid") String gid);

    //    @Delete("delete from x_user_ext_info where id = #{id}") // 物理删除
    int delete(@Param("userGid") String gid);

    int update(XUserExtInfo xUserExtInfo);

    int count(@Param("params") Map<String, Object> params);

    List<XUserExtInfo> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
