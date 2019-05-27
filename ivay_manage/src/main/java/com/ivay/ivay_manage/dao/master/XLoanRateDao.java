package com.ivay.ivay_manage.dao.master;

import com.ivay.ivay_manage.model.XLoanRate;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XLoanRateDao {

    @Select("select * from x_loan_rate t where t.user_gid = #{userGid} and t.enable_flag='Y'")
    List<XLoanRate> getByGid(String userGid);

    /**
     * 获取利率
     *
     * @param userGid
     * @param period
     * @return
     */
    @Select("select * from x_loan_rate t where t.user_gid = #{userGid} and t.period = #{period}")
    XLoanRate getByUserAndPeriod(String userGid, Integer period);

    @Delete("delete from x_loan_rate where id = #{id}")
    int delete(Long id);

    int update(XLoanRate xLoanRate);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_loan_rate(user_gid, fee_rate, interest_rate, period, create_time, update_time, enable_flag) values(#{userGid}, #{feeRate}, #{interestRate}, #{period}, #{createTime}, #{updateTime}, #{enableFlag})")
    int save(XLoanRate xLoanRate);

    int saveByBatch(List<XLoanRate> list);

    int count(@Param("params") Map<String, Object> params);

    List<XLoanRate> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
