package com.ivay.ivay_app.dao;

import com.ivay.ivay_app.model.XUserContacts;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XUserContactsDao {

    @Select("select * from x_user_contacts t where t.id = #{id}")
    XUserContacts getById(Long id);

    @Delete("delete from x_user_contacts where id = #{id}")
    int delete(Long id);

    int update(XUserContacts xUserContacts);
    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_user_contacts(user_gid, update_date, contact_name, phone_number) values(#{userGid}, #{updateDate}, #{contactName}, #{phoneNumber})")
    int save(XUserContacts xUserContacts);
    
    int count(@Param("params") Map<String, Object> params);

    List<XUserContacts> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);

    boolean insertBatchContacts(List<XUserContacts> xUserContacts);
}
