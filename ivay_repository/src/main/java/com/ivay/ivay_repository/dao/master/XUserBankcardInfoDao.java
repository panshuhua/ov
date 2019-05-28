package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XBankAndCardInfo;
import com.ivay.ivay_repository.model.XUserBankcardInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XUserBankcardInfoDao {

    @Select("select * from x_user_bankcoad_info t where t.id = #{id} and enable_flag='Y' ")
    XUserBankcardInfo getById(Long id);

    @Select("select * from x_user_bankcoad_info t where t.user_gid = #{userGid} and enable_flag='Y' ")
    List<XUserBankcardInfo> getByUserGid(String userGid);

    @Select("select * from x_user_bankcoad_info t where t.card_no = #{cardNo} and enable_flag='Y' ")
    List<XUserBankcardInfo> getByCardNo(String cardNo);

    XBankAndCardInfo getBankAndCardByGid(@Param("bankcardGid") String bankcardGid, @Param("userGid") String userGid);

    @Select("select bankcard_gid, bank_gid, user_gid, card_user_name, card_no, status from x_user_bankcoad_info t where t.bankcard_gid = #{bankcardGid} and user_gid = #{userGid} and enable_flag='Y' ")
    List<XUserBankcardInfo> getByCardGid(@Param("bankcardGid") String bankcardGid, @Param("userGid") String userGid);

    @Delete("delete from x_user_bankcoad_info where bankcard_gid = #{bankcardGid} and user_gid = #{userGid} and enable_flag='Y' ")
    int delete(@Param("bankcardGid") String bankcardGid, @Param("userGid") String userGid);

    @Delete("delete from x_user_bankcoad_info where bankcard_gid = #{bankcardGid} and enable_flag='Y' ")
    int deletes(@Param("bankcardGid") String bankcardGid);

    int update(XUserBankcardInfo xUserBankcardInfo);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_user_bankcoad_info(bankcard_gid, bank_gid, user_gid, card_user_name, acc_type, card_no, status, create_time, update_time, enable_flag)" +
            "values(#{bankcardGid}, #{bankGid}, #{userGid}, #{cardUserName}, #{accType}, #{cardNo}, #{status}, #{createTime}, #{updateTime}, #{enableFlag})")
    int save(XUserBankcardInfo xUserBankcardInfo);

    int count(@Param("params") Map<String, Object> params);

    List<XUserBankcardInfo> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
