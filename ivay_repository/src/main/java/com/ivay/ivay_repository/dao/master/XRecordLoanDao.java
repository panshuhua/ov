package com.ivay.ivay_repository.dao.master;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.ivay.ivay_repository.dto.XOverDueFee;
import com.ivay.ivay_repository.dto.XRecordLoanInfo;
import com.ivay.ivay_repository.model.XRecordLoan;

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
     * 找出某个用户的所有借款记录，会置顶未还清借款
     *
     * @param params
     * @param offset
     * @param limit
     * @return
     */
    List<XRecordLoan> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
        @Param("limit") Integer limit);

    @Select("select IFNULL(SUM(t.due_amount+t.overdue_fee+t.overdue_interest),0) from x_record_loan t"
        + " where t.user_gid = #{userGid} AND t.loan_status='1' AND (t.repayment_status ='0' OR t.repayment_status='1')")
    long getSumLoanAmount(String userGid);

    @Select("SELECT min(x.last_repayment_time) FROM x_record_loan x WHERE x.user_gid=#{userGid} AND x.repayment_status='2'")
    Date getFirstRepaymentTime(String userGid);

    @Select("select * from x_record_loan where order_id=#{orderId}")
    XRecordLoan getXRecordLoanByOrderId(String orderId);

    @Select("SELECT r.order_id, r.user_gid, u.`name`,r.overdue_fee,r.overdue_interest,r.due_amount FROM x_record_loan r LEFT JOIN x_user_info u ON r.user_gid = u.user_gid WHERE r.loan_status = 1 AND r.repayment_status != 2 and r.order_id NOT in(SELECT order_id from x_virtual_account x where x.response_code=200)")
    List<XRecordLoanInfo> findRecordLoanInfo();

    // 查询明天/今天到期的用户-到期前1天/到期当天提醒
    @Select("SELECT r.user_gid,r.gid,r.due_time,r.due_amount,u.fmc_token,u.phone,r.loan_period,r.loan_rate "
        + "FROM x_record_loan r LEFT JOIN x_user_info u ON r.user_gid = u.user_gid "
        + "WHERE(r.due_time <= date_format(DATE_ADD(sysdate(), INTERVAL +2 DAY),'%Y%m%d')AND r.due_time >= date_format(sysdate(),'%Y%m%d'))"
        + "AND u.id IS NOT NULL AND r.repayment_status != 2 AND r.loan_status = 1 ")
    List<XOverDueFee> findOneOverdue();

    // 查询逾期的用户-逾期提醒
    @Select("SELECT r.user_gid,r.overdue_fee,r.overdue_interest,r.gid,r.due_time,r.due_amount,u.fmc_token, DATEDIFF(SYSDATE(),r.due_time)due_date FROM x_record_loan r LEFT JOIN x_user_info u ON r.user_gid = u.user_gid WHERE( r.due_time <= date_format(sysdate(), '%Y%m%d'))AND u.id IS NOT NULL AND r.repayment_status != 2 AND r.loan_status = 1 ")
    List<XOverDueFee> findOverdue();

    int countOverDueLoan(@Param("params") Map<String, Object> params);

    List<XRecordLoan> overDueLoan(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
        @Param("limit") Integer limit);
}
