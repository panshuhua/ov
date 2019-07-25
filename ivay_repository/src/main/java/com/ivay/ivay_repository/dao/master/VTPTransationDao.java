package com.ivay.ivay_repository.dao.master;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.ivay.ivay_repository.dto.VTPTranProcessInfo;
import com.ivay.ivay_repository.dto.XVtpRepayTranInfo;

@Mapper
public interface VTPTransationDao {
    /**
     * VTP还款接口所需用户信息
     * 
     * @param orderId
     * @return
     */
    @Select("SELECT x.order_id, SUM(x.overdue_fee + x.overdue_interest + x.due_amount) AS shouldRepayAmount,i.* FROM x_record_loan x LEFT JOIN x_user_info i ON x.user_gid = i.user_gid WHERE order_id = #{orderId} GROUP BY i.id")
    XVtpRepayTranInfo queryRepayTranInfo(@Param("orderId") String orderId);

    /**
     * 保存VTP交易信息
     * 
     * @param info
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO `vtp_transaction_process_input` (`trans_id`, `ref_id`, `customer_code`, `amount`, `fee`, `content`, `agent_user`, `agent_name`, `trans_date`, `create_time`, `update_time`, `enable_flag`, `trans_msg`, `trans_status`) VALUES (#{transId}, #{refId}, #{customerCode}, #{amount}, #{fee}, #{content}, #{agentUser}, #{agentName}, #{transDate}, now(),now(), 'Y', #{transMsg}, #{transStatus})")
    Integer saveTranProcessInfo(VTPTranProcessInfo info);

    /**
     * 查询交易状态
     * 
     * @param transId
     * @return
     */
    @Select("select * from vtp_transaction_process_input where trans_id=#{transId}")
    VTPTranProcessInfo queryTransStatus(@Param("transId") String transId);

}
