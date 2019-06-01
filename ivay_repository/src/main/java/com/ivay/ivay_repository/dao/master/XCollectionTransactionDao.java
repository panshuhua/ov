package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XCollectionTransaction;
import org.apache.ibatis.annotations.*;

@Mapper
public interface XCollectionTransactionDao {

    @Select("select * from x_collection_transaction where request_id=#{requestId}")
    String findRequestId(@Param("requestId") String requestId);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_collection_transaction (request_id, request_time, partner_code, acc_no, clientid_no, trans_id, trans_amount, trans_time, beftrans_debt, afftrans_debt, account_type, order_id, create_time, update_time, enable_flag) VALUES (#{requestId}, #{requestTime}, #{partnerCode}, #{accNo}, #{clientidNo}, #{transId}, #{transAmount}, #{transTime}, #{beftransDebt}, #{afftransDebt}, #{accountType}, #{orderId}, #{createTime}, #{updateTime}, #{enableFlag})")
    int insert(XCollectionTransaction xCollectionTransaction);

    @Select("select collect_amount from x_virtual_account where acc_no=#{accNo}")
    long getCollectAmount(String accNo);

    @Select("select count(1) from x_collection_transaction where trans_id=#{transId}")
    int queryByTransId(String transId);

}
