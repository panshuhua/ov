package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XRecordRepayment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XRecordRepaymentDao {
    @Select("select * from x_record_repayment t where t.gid = #{gid} and t.user_gid = #{userGid}")
    XRecordRepayment getByGid(String gid, String userGid);

    /**
     * 按条件查询满足条件的还款记录
     *
     * @param xRecordRepayment
     * @return
     */
    List<XRecordRepayment> getSelective(XRecordRepayment xRecordRepayment);

    @Delete("delete from x_record_repayment where id = #{id}")
    int delete(Long id);

    int update(XRecordRepayment xRecordRepayment);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_record_repayment(gid, create_time, update_time, user_gid, record_loan_gid, repayment_type, repayment_way, repayment_amount, repayment_status, end_time, repayment_overdue_fee, fail_reason) values(#{gid}, #{createTime}, #{updateTime}, #{userGid}, #{recordLoanGid}, #{repaymentType}, #{repaymentWay}, #{repaymentAmount}, #{repaymentStatus}, #{endTime}, #{repaymentOverdueFee}, #{failReason})")
    int save(XRecordRepayment xRecordRepayment);

    int count(@Param("params") Map<String, Object> params);

    List<XRecordRepayment> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
