package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.dto.SalesRecordResult;
import com.ivay.ivay_repository.model.XSalesRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XSalesRecordDao {

    @Select("select * from x_sales_record t where t.id = #{id}")
    XSalesRecord getById(Long id);

    @Delete("delete from x_sales_record where id = #{id}")
    int delete(Long id);

    int update(XSalesRecord xSalesRecord);
    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_sales_record(risk_user_id, salesman_id, content, create_time, update_time) values(#{riskUserId}, #{salesmanId}, #{content}, #{createTime}, #{updateTime})")
    int insert(XSalesRecord xSalesRecord);
    
    int count(@Param("params") Map<String, Object> params);

    List<XSalesRecord> list(@Param("params") Map<String, Object> params,
                          @Param("offset") Integer offset,
                          @Param("limit") Integer limit);

    /**
     * @Description 查询销售记录数量
     * @Author Ryan
     * @Param [id]
     * @Return int
     * @Date 2019/7/23 17:58
     */
    int getSalesRecordListCount(@Param("id") int id);

    /**
     * @Description 查询销售记录
     * @Author Ryan
     * @Param [id, offset, limit]
     * @Return java.util.List<?>
     * @Date 2019/7/23 17:59
     */
    List<SalesRecordResult> getSalesRecordList(@Param("id") int id, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
