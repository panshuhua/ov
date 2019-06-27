package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.dto.*;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XUserInfo;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface XUserInfoDao {

    @Select("select * from x_user_info where phone=#{mobile} and password=#{password} and enable_flag='Y'")
    XUser getLoginUser(@Param("mobile") String mobile, @Param("password") String password);

    @Select("select user_gid from x_user_info where phone=#{mobile} and enable_flag='Y'")
    String getUserGid(@Param("mobile") String mobile);

    @Select("select password from x_user_info where phone=#{mobile} and enable_flag='Y'")
    String getPassword(@Param("mobile") String mobile);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_user_info(phone,password,user_gid,create_time,user_status,account_status, enable_flag,mac_code," +
            " update_time,fmc_token,refuse_reason,refuse_type,audit_time)" +
            " values(#{phone},#{password},#{userGid},#{createTime}, #{userStatus}, #{accountStatus}, #{enableFlag},#{macCode}," +
            " #{updateTime},#{fmcToken},#{refuseReason},#{refuseType},#{auditTime})")
    int addUser(XUserInfo xUserInfo);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_user_info(phone, user_gid, name, identity_card, birthday, sex, education, marital, place, income," +
            " create_time, update_time,user_status,account_status, enable_flag,credit_line,credit_line_count,canborrow_amount,trans_pwd," +
            " refuse_reason,refuse_type,audit_time)" +
            " values(#{phone}, #{userGid}, #{name}, #{identityCard}, #{birthday}, #{sex}, #{education}, #{marital}, #{place}, #{income}," +
            " #{createTime}, #{updateTime}, #{userStatus}, #{accountStatus}, #{enableFlag},#{credit_line},#{credit_line_count},#{canborrow_amount},#{trans_pwd}," +
            "#{refuseReason},#{refuseType},#{auditTime})")
    int save(XUserInfo xUserInfo);

    @Select("select * from x_user_info t where t.user_gid = #{gid} and t.enable_flag='Y' and t.account_status='0'")
    XUserInfo getByGid(String gid);

    @Select("select phone from x_user_info t where t.user_gid = #{gid} and t.enable_flag='Y' and t.account_status='0'")
    String getPhone(String gid);

    @Select("select * from x_user_info t where t.identity_card = #{identityCard}")
    List<XUserInfo> getByIdentityCard(String identityCard);

    int update(XUserInfo xUserInfo);

    int delete(@Param("userGid") String gid);

    int count(@Param("params") Map<String, Object> params);

    List<XUserInfo> list(@Param("params") Map<String, Object> params,
                         @Param("offset") Integer offset,
                         @Param("limit") Integer limit);

    int auditCount(@Param("params") Map<String, Object> params);

    List<XAuditCondition> auditList(@Param("params") Map<String, Object> params,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    @Select("select user_gid,credit_line,credit_line_count,canborrow_amount,user_status from x_user_info t" +
            " where t.user_gid = #{gid} and t.enable_flag='Y'")
    CreditLine getCreditLine(String gid);

    @Select("select user_status from x_user_info where user_gid=#{gid}")
    String getUserStatus(String gid);

    @Select("select trans_pwd from x_user_info where user_gid=#{gid}")
    String getTranPwd(String gid);

    @Update("update x_user_info set trans_pwd=#{transPwd} where user_gid=#{userGid}")
    int setTransPwd(@Param("userGid") String gid, @Param("transPwd") String transPwd);

    @Update("update x_user_info set user_status=1 where user_gid=#{gid}")
    int updateUseStatus(String gid);

    @Update("update x_user_info set password=#{password} where phone=#{mobile}")
    int updatePassword(@Param("mobile") String mobile, @Param("password") String password);

    @Update("update x_user_info set canborrow_amount=#{canborrowAmount} where user_gid=#{userGid}")
    int updateCanborrowAmount(@Param("canborrowAmount") long canborrowAmount, @Param("userGid") String userGid);

    XAuditDetail auditDetail(String userGid);

    @Select("select identity_card from x_user_info where user_gid=#{gid}")
    String getIdentityCardByGid(String gid);

    @Select("select * from x_record_loan where order_id=#{orderId}")
    XRecordLoan getUserGidByOrderId(String orderId);

    @Select("select count(1) from x_user_info where identity_card=#{identityCard}")
    int countIdentityCard(String identityCard);

    @Select("select count(1) from x_user_ext_info where major_phone=#{phone}")
    int countMajorPhone(String phone);

    @Select("select count(1) from x_user_info where mac_code=#{macCode}")
    int countOnePhone(String macCode);

    // 统计14天内 最大的联系人个数
    @Select("SELECT IFNULL(max(num),0) from ( " +
            "select count(1) as num from x_user_contacts where user_gid=#{userGid}" +
            " AND DATEDIFF(date_format(now(), '%Y-%m-%d'),update_date)<=14 GROUP BY update_date ) temp")
    int countContacts(String userGid);

    /**
     * 贷前策略
     *
     * @param userGid
     * @return
     */
    XLoanQualification getAuditQualificationObj(String userGid);

    /**
     * 贷中策略-查询历史最大逾期天数
     *
     * @param userGid
     * @return
     */
    Integer getMaxOverdueDay(String userGid);

    /**
     * 贷中策略-查询当前逾期还款的条数
     *
     * @param userGid
     * @return
     */
    Integer getOverdueCountsNow(String userGid);

    /**
     * 贷中策略-最近一笔结清交易逾期天数
     *
     * @param userGid
     * @return
     */
    Integer getlastOverdueDay(String userGid);

    /**
     * 贷前/贷中策略-查询当前用户gps位置10m内所有注册用户的个数
     *
     * @param longitude
     * @param latitude
     * @return
     */
    Integer getUserCountsBygps(@Param("longitude") BigDecimal longitude, @Param("latitude") BigDecimal latitude);

    /**
     * 查出待审核用户
     *
     * @param num 天数
     * @return
     */
    List<XUserInfo> toBeAuditedList(@Param("num") Integer num);

    @Update("update x_user_info set fmc_token=#{fmcToken} where user_gid=#{userGid}")
    int updateTmcToken(@Param("fmcToken") String fmcToken, @Param("userGid") String userGid);

    //查询审核通过的用户的fmc_token
    @Select("select * from x_user_info where user_status='3'")
    List<XUserInfo> findAuditPassUsers();

    //查询放款成功的用户的fmc_token
    @Select("select * from x_user_info where user_status='5'")
    List<XUserInfo> findLoanSuccessUsers();

    //查询到期还款的用户的fmc_token
    @Select("SELECT u.* FROM x_record_loan r LEFT JOIN x_user_info u ON r.user_gid = u.user_gid WHERE (r.due_time <= date_format(DATE_ADD(sysdate(), INTERVAL 2 DAY), '%Y%m%d') and r.due_time >= date_format(DATE_ADD(sysdate(), INTERVAL -1 DAY), '%Y%m%d')) AND u.id IS NOT NULL and r.repayment_status !=2 and r.loan_status=1")
    List<XUserInfo> findShouldRepaymentUsers();

    int countSameName(@Param("params") Map<String, Object> params);

    List<XAuditCondition> listSameName(@Param("params") Map<String, Object> params,
                                 @Param("offset") Integer offset,
                                 @Param("limit") Integer limit);

    int countOverDueUsers(@Param("params") Map<String, Object> params);

    List<XUserInfo> overDueUsers(@Param("params") Map<String, Object> params,
                                 @Param("offset") Integer offset,
                                 @Param("limit") Integer limit);

    @Select("select phone from x_user_info where mac_code=#{macCode} and enable_flag='Y' and user_status!='7' and account_status='0'")
    List<String> checkMacCode(String macCode);
}
