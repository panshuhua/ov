package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XBaokimTransfersInfo;
import com.ivay.ivay_repository.model.XEbayTransfersInfo;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XBaokimTransfersInfoDao {

    @Select("select * from x_baokim_transfers_info t where t.id = #{id}")
    XBaokimTransfersInfo getById(Long id);
    
    @Select("select * from x_baokim_transfers_info t where t.id = #{id}")
    XEbayTransfersInfo getEbayInfoById(Long id);

    @Delete("delete from x_baokim_transfers_info where id = #{id}")
    int delete(Long id);

    int update(XBaokimTransfersInfo xBaokimTransfersInfo);
    
    int updateEbayInfo(XEbayTransfersInfo xEbayTransfersInfo);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_baokim_transfers_info(request_id, request_time, partner_code, operation, bank_no, acc_no, acc_name, acc_type, reference_id, request_amount, memo, response_code, response_message, transaction_id, transaction_time, transfer_amount, create_time, update_time, enable_flag) values(#{requestId}, #{requestTime}, #{partnerCode}, #{operation}, #{bankNo}, #{accNo}, #{accName}, #{accType}, #{referenceId}, #{requestAmount}, #{memo}, #{responseCode}, #{responseMessage}, #{transactionId}, #{transactionTime}, #{transferAmount}, #{createTime}, #{updateTime}, #{enableFlag})")
    int save(XBaokimTransfersInfo xBaokimTransfersInfo);
    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_baokim_transfers_info(request_id, request_time, partner_code, operation, bank_no, acc_no, acc_name, acc_type, reference_id, request_amount, memo, response_code, response_message, transaction_id, transaction_time, transfer_amount, create_time, update_time, enable_flag,contract_number,extend,sub_error_code,sub_error_message,reason) values(#{requestId}, #{requestTime}, #{partnerCode}, #{operation}, #{bankNo}, #{accNo}, #{accName}, #{accType}, #{referenceId}, #{requestAmount}, #{memo}, #{responseCode}, #{responseMessage}, #{transactionId}, #{transactionTime}, #{transferAmount}, #{createTime}, #{updateTime}, #{enableFlag},#{contractNumber},#{extend},#{subErrorCode},#{subErrorMessage},#{reason})")
    int saveEbayInfo(XEbayTransfersInfo xEbayTransfersInfo);

    int count(@Param("params") Map<String, Object> params);

    List<XBaokimTransfersInfo> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    List<XEbayTransfersInfo> listEbayInfo(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
