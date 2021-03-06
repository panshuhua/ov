package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XVirtualAccount;
import org.apache.ibatis.annotations.*;

@Mapper
public interface XVirtualAccountDao {

    @Select("select * from x_virtual_account where id=#{id}")
    XVirtualAccount queryVirtualAccount(String id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_virtual_account(acc_no, acc_name, clientid_no, issued_date, issued_place, collect_amount, order_id, expire_date, account_type, create_time, update_time, enable_flag,response_code,response_message,request_id) VALUES (#{accNo},#{accName},#{clientidNo},#{issuedDate},#{issuedPlace},#{collectAmount},#{orderId},#{expireDate},#{accountType},#{createTime},#{updateTime},#{enableFlag},#{responseCode},#{responseMessage},#{requestId})")
    int insert(XVirtualAccount xVirtualAccount);

    @Select("select * from x_virtual_account where order_id=#{orderId} and response_code=200")
    XVirtualAccount selectByOrderId(String orderId);

    @Update("update x_virtual_account set collect_amount=#{collectAmount},update_time=#{updateTime},request_id=#{requestId},response_code=#{responseCode},response_message=#{responseMessage} where order_id=#{orderId} and response_code=200")
    int update(XVirtualAccount xVirtualAccount);

}
