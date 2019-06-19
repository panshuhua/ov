package com.ivay.ivay_repository.dao.master;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ivay.ivay_repository.model.XEbayVirtualAccount;

@Mapper
public interface XEbayVirtualAccountDao {
	 @Options(useGeneratedKeys = true, keyProperty = "id")
	 @Insert("INSERT INTO x_ebay_virtual_account (pcode, merchant_code, map_id, amount, start_date, end_date, `condition`, customer_name, request_id, response_code, message, account_no, account_name, bank_code, bank_name,create_time,update_time,enable_flag) VALUES (#{pcode}, #{merchantCode}, #{mapId}, #{amount}, #{startDate}, #{endDate}, #{condition}, #{customerName}, #{requestId}, #{responseCode}, #{message}, #{accountNo}, #{accountName}, #{bankCode}, #{bankName},now(),now(),'Y')")
     int sava(XEbayVirtualAccount xEbayVirtualAccount);
	 
	 @Update("UPDATE x_ebay_virtual_account SET account_no=#{accountNo},amount=#{amount},start_date=#{startDate}, end_date=#{endDate}, `condition`=#{condition}, customer_name=#{customerName}, request_id=#{requestId}, response_code=#{responseCode},message=#{message}, account_name=#{accountName}, bank_code=#{bankCode}, bank_name=#{bankName},update_time=now() WHERE map_id=#{mapId} and response_code=00")
	 int update(XEbayVirtualAccount xEbayVirtualAccount);
	 
	 @Delete("delete from x_ebay_virtual_account where account_no=#{accountNo} and enable_flag='Y'")
	 int delete(XEbayVirtualAccount xEbayVirtualAccount);
	 
	 @Select("select * from x_ebay_virtual_account where map_id=#{mapId} and response_code=00")
	 int findAccountNo(String mapId);
}
