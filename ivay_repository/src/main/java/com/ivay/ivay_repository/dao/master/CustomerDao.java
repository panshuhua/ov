package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerDao {

    @Select("select path,url from file_info where id=#{id}")
    XFileInfo queryphotoUrl(String id);

    int countBasicInfo(@Param("params") Map<String, Object> params);

    List<XUserInfo> listBasicInfo(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);

    int countContactInfo(@Param("params") Map<String, Object> params);

    List<XUserExtInfo> listContactInfo(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

    int countLoan(@Param("params") Map<String, Object> params);

    List<XRecordLoan> listLoan(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    int countRepay(@Param("params") Map<String, Object> params);

    List<XRecordRepayment> listRepay(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    int countBank(@Param("params") Map<String, Object> params);

    List<XUserBankcoadInfo> listBank(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

}
