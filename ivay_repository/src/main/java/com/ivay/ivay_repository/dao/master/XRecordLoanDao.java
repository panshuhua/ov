package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XRecordLoan;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface XRecordLoanDao {

    @Select("select * from x_record_loan t where t.gid = #{gid} and t.user_gid = #{userGid}")
    XRecordLoan getByGid(@Param("gid") String gid, @Param("userGid") String userGid);

    @Delete("delete from x_record_loan where id = #{id}")
    int delete(Long id);

    int update(XRecordLoan xRecordLoan);

    int updateByBatch(@Param("list") List<XRecordLoan> list);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_record_loan(gid, order_id, user_gid, bankcard_gid, product_id, loan_amount,loan_period, loan_rate, loan_time,"
            + "fee,interest,memo, net_amount, due_amount, due_time,overdue_fee,overdue_fee_total, overdue_interest,overdue_interest_total,"
            + "loan_status, repayment_status,last_repayment_time, fail_reason,create_time, update_time)"
            + "values(#{gid}, #{orderId}, #{userGid}, #{bankcardGid },#{productId},#{loanAmount},#{loanPeriod}, #{loanRate}, #{loanTime},"
            + "#{fee},#{interest},#{memo}, #{netAmount},#{dueAmount}, #{dueTime}, #{overdueFee},#{overdueFeeTotal},#{overdueInterest}, #{overdueInterestTotal},"
            + "#{loanStatus}, #{repaymentStatus}, #{lastRepaymentTime}, #{failReason},#{createTime}, #{updateTime})")
    int save(XRecordLoan xRecordLoan);

    int count(@Param("params") Map<String, Object> params);

    /**
     * 找出某个用户的所有借款成功的记录，会置顶未还清借款
     *
     * @param params
     * @param offset
     * @param limit
     * @return
     */
    List<XRecordLoan> list(@Param("params") Map<String, Object> params,
                           @Param("offset") Integer offset,
                           @Param("limit") Integer limit);

    @Select("select IFNULL(SUM(t.due_amount+t.overdue_fee+t.overdue_interest),0) from x_record_loan t"
            + " where t.user_gid = #{userGid} AND t.loan_status='1' AND (t.repayment_status ='0' OR t.repayment_status='1')")
    long getSumLoanAmount(String userGid);

    @Select("SELECT min(x.last_repayment_time) FROM x_record_loan x WHERE x.user_gid=#{userGid} AND x.repayment_status='2'")
    Date getFirstRepaymentTime(String userGid);

    @Select("select * from x_record_loan where order_id=#{orderId}")
    XRecordLoan getXRecordLoanByOrderId(String orderId);

    @Select("SELECT r.order_id order_id, r.user_gid user_gid, u.`name` `name`,r.overdue_fee overdue_fee,r.overdue_interest overdue_interest,r.due_amount due_amount FROM  x_record_loan r LEFT JOIN x_user_info u ON r.user_gid = u.user_gid WHERE    r.loan_status = 1 AND r.repayment_status != 2 AND TO_DAYS(r.create_time) = TO_DAYS(NOW())")
    List<Map<String, Object>> findRecordLoanInfo();

}
