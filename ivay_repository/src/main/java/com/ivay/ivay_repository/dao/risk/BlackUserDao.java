package com.ivay.ivay_repository.dao.risk;

import com.ivay.ivay_repository.model.BlackUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BlackUserDao {

    @Select("select * from black_user t where t.id = #{id}")
    BlackUser getById(Long id);

    @Delete("delete from black_user where id = #{id}")
    int delete(Long id);

    int update(BlackUser blackUser);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into black_user(phone, identity_card) values(#{phone}, #{identityCard})")
    int save(BlackUser blackUser);

    int count(@Param("params") Map<String, Object> params);

    List<BlackUser> list(@Param("params") Map<String, Object> params,
                         @Param("offset") Integer offset,
                         @Param("limit") Integer limit);

    List<BlackUser> selectBlackUsers(@Param("phone") String phone,
                                @Param("identityCard") String identityCard);
}
