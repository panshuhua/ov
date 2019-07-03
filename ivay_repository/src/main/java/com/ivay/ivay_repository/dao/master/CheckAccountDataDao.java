package com.ivay.ivay_repository.dao.master;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.ivay.ivay_repository.model.AccountCheckResult;
import com.ivay.ivay_repository.model.BaokimCollectionData;
import com.ivay.ivay_repository.model.BaokimTransferData;

/**
 * 通道对账
 * 
 * @author panshuhua
 * @date 2019/07/03
 */
@Mapper
public interface CheckAccountDataDao {

    Integer insertBatchBkCollTrans(List<BaokimCollectionData> trans);

    // 根据时间计算交易的条数-还款
    @Select("select count(1) baokimCount,sum(amount) baokimAmount from baokim_collection_data where time_recorded like CONCAT('%',#{time},'%') and status='Success'")
    AccountCheckResult countByBkCollection(@Param("time") String time);

    @Select("SELECT count(1) ovayCount,sum(repayment_amount) ovayAmount FROM x_record_repayment WHERE create_time like CONCAT('%',#{time},'%')  and repayment_status=2")
    AccountCheckResult countByOvayCollection(@Param("time") String time);

    Integer insertBatchBkTransfers(List<BaokimTransferData> trans);

    // 借款
    @Select("SELECT count(1) baokimCount,sum(amount) baokimAmount FROM  baokim_transfer_data WHERE trans_time like CONCAT('%',#{time},'%')  and status='Thành công'")
    AccountCheckResult countByBkTransfer(@Param("time") String time);

    // TODO 跟baokim对账的金额是哪个字段？
    @Select("SELECT count(1) ovayCount,sum(overdue_fee+overdue_interest+due_amount) ovayAmount FROM `x_record_loan` WHERE create_time like CONCAT('%',#{time},'%') and loan_status=1")
    AccountCheckResult countByOvayTransfer(@Param("time") String time);

    // 存储对账结果
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO `account_check_result` (`baokim_count`, `baokim_amount`, `ovay_count`, `ovay_amount`, `type`, `partner`,`enable_flag`,`create_time`,`update_time`,`time`) VALUES (#{baokimCount}, #{baokimAmount}, #{ovayCount}, #{ovayAmount}, #{type}, #{partner},'Y',now(),now(),#{time})")
    Integer insertResult(AccountCheckResult result);

}
