package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XConfigDao {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_config(type, content, description, lang) values(#{type}, #{content}, #{description}, #{lang})")
    int save(XConfig xConfig);

    @Select("select * from x_config t where t.type = #{type}")
    XConfig getByType(String type);

    @Select("select * from x_config t where t.type = #{type} and t.lang = #{lang}")
    XConfig getByTypeAndLang(@Param("type") String type, @Param("lang") String lang);

    int update(XConfig xConfig);

    @Delete("delete from x_config where id = #{id}")
    int delete(Long id);


    int count(@Param("params") Map<String, Object> params);

    List<XConfig> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
