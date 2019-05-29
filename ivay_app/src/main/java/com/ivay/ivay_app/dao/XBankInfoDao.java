package com.ivay.ivay_app.dao;

import com.ivay.ivay_app.model.XBankInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XBankInfoDao {

    @Select("select * from x_bank_info t where t.id = #{id}")
    XBankInfo getById(Long id);

    @Select("select * from x_bank_info t where t.bank_gid = #{gid}")
    XBankInfo getByGid(String gid);
    
    @Select("select bank_gid, bank_no, bank_name, is_account, is_card from x_bank_info t where t.enable_flag='Y' ")
    List<XBankInfo> getBankList();

    @Delete("delete from x_bank_info where id = #{id}")
    int delete(Long id);

    int update(XBankInfo xBankInfo);
    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_bank_info(bank_gid, bank_no, bank_name, is_account, is_card, create_time, update_time, enable_flag) values(#{bankGid}, #{bankNo}, #{bankName}, #{isAccount}, #{isCard}, #{createTime}, #{updateTime}, #{enableFlag})")
    int save(XBankInfo xBankInfo);
    
    int count(@Param("params") Map<String, Object> params);

    List<XBankInfo> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
