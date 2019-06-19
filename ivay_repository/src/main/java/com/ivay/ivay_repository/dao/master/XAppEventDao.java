package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XAppEvent;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XAppEventDao {
    @Select("select * from x_app_event t where t.gid = #{gid} and t.enable_flag = 'Y'")
    XAppEvent getByGid(String gid);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_app_event(gid, type, is_success, enable_flag, create_time, update_time) " +
            "values(#{gid}, #{type}, #{isSuccess}, #{enableFlag}, #{createTime}, #{updateTime})")
    int save(XAppEvent xAppEvent);

    int update(XAppEvent xAppEvent);

    int delete(@Param("gids") String[] gids);

    int count(@Param("params") Map<String, Object> params);

    List<XAppEvent> list(@Param("params") Map<String, Object> params,
                         @Param("offset") Integer offset,
                         @Param("limit") Integer limit);

    List<XAppEvent> listToBeUpload(String userGid);
}
