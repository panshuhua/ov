package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XCollectionCalculate;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XCollectionCalculateDao {

    @Select("select * from x_collection_calculatesys_user t where t.id = #{id}")
    XCollectionCalculate getById(Long id);

    @Delete("delete from x_collection_calculatesys_user where id = #{id}")
    int delete(Long id);

    int update(XCollectionCalculate xCollectionCalculate);
    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_collection_calculatesys_user(calculate_date, overdue_order, overdue_user, overdue_principal, amount_receivable, number_repay, amount_repay, create_time, update_time) values(#{calculateDate}, #{overdueOrder}, #{overdueUser}, #{overduePrincipal}, #{amountReceivable}, #{numberRepay}, #{amountRepay}, #{createTime}, #{updateTime})")
    int save(XCollectionCalculate xCollectionCalculate);
    
    int count(@Param("params") Map<String, Object> params);

    List<XCollectionCalculate> list(@Param("params") Map<String, Object> params,
                          @Param("offset") Integer offset,
                          @Param("limit") Integer limit);
}
