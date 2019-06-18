package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XAppEvent;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XAppEventDao {

    @Select("select * from x_app_event t where t.id = #{id} and t.enable_flag = 'Y'")
    XAppEvent getById(Long id);

    @Select("select * from x_app_event t where t.gid = #{gid} and t.enable_flag = 'Y'")
    XAppEvent getByGid(String gid);

    @Delete("delete from x_app_event where id = #{id} and t.enable_flag = 'Y'")
    int delete(Long id);

    int update(XAppEvent xAppEvent);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_app_event(gid, type, is_upload, enable_flag, create_time, update_time) values(#{gid}, #{type}, #{isUpload}, #{enableFlag}, #{createTime}, #{updateTime})")
    int save(XAppEvent xAppEvent);

    int count(@Param("params") Map<String, Object> params);

    List<XAppEvent> list(@Param("params") Map<String, Object> params,
                         @Param("offset") Integer offset,
                         @Param("limit") Integer limit);
}
