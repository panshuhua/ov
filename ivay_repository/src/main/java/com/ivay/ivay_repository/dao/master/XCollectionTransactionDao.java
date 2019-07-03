package com.ivay.ivay_repository.dao.master;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.ivay.ivay_repository.model.XCollectionTransaction;
import com.ivay.ivay_repository.model.XEbayCollectionNotice;

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

    // ebay-findrefenceId
    @Select("select * from x_ebay_collection_notice where reference_id=#{referenceId}")
    String findRefenceId(@Param("referenceId") String referenceId);

    // ebay-保存回调请求数据
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Select("insert into x_ebay_collection_notice (request_id, request_time, bank_tran_time, reference_id, map_id, amount, merchant_code, fee, va_name, va_acc, bank_code, bank_name, enable_flag, create_time, update_time) VALUES (#{requestId}, #{requestTime}, #{bankTranTime}, #{referenceId}, #{mapId}, #{amount}, #{merchantCode}, #{fee}, #{vaName}, #{vaAcc}, #{bankCode}, #{bankName}, 'Y', now(), now())")
    Integer insertEbayNotice(XEbayCollectionNotice xEbayCollectionNotice);

}
