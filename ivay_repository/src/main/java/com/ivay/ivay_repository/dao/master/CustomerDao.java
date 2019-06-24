package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.dto.XRecordLoan2;
import com.ivay.ivay_repository.dto.XRecordRepayment2;
import com.ivay.ivay_repository.dto.XUserCardAndBankInfo;
import com.ivay.ivay_repository.model.XConfig;
import com.ivay.ivay_repository.model.XFileInfo;
import com.ivay.ivay_repository.model.XUserExtInfo;
import com.ivay.ivay_repository.model.XUserInfo;
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

    List<XUserInfo> listBasicInfo(@Param("params") Map<String, Object> params,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);

    int countContactInfo(@Param("params") Map<String, Object> params);

    List<XUserExtInfo> listContactInfo(@Param("params") Map<String, Object> params,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

    int countLoan(@Param("params") Map<String, Object> params);

    List<XRecordLoan2> listLoan(@Param("params") Map<String, Object> params,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    int countRepay(@Param("params") Map<String, Object> params);

    List<XRecordRepayment2> listRepay(@Param("params") Map<String, Object> params,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    int countBank(@Param("params") Map<String, Object> params);

    List<XUserCardAndBankInfo> listBank(@Param("params") Map<String, Object> params,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    @Select("select * from x_config where type=#{type} and lang=#{lang}")
    XConfig findConfigByType(@Param("type") String type, @Param("lang") String lang);

}
