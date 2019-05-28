package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.Mail;
import com.ivay.ivay_repository.model.MailTo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface MailDao {

    @Select("select * from t_mail t where t.id = #{id}")
    Mail getById(Long id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into t_mail(userId, subject, content, createTime, updateTime) values(#{userId}, #{subject}, #{content}, now(), now())")
    int save(Mail mail);

    @Insert("insert into t_mail_to(mailId, toUser, status) values(#{mailId}, #{toUser}, #{status})")
    int saveToUser(@Param("mailId") Long mailId, @Param("toUser") String toUser, @Param("status") int status);

    @Select("select t.* from t_mail_to t where t.mailId = #{mailId}")
    List<MailTo> getToUsers(Long mailId);

    int count(@Param("params") Map<String, Object> params);

    List<Mail> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                    @Param("limit") Integer limit);
}
