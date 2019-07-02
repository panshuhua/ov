package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XCollectionTask;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XCollectionTaskDao {

    @Select("select * from x_collection_task t where t.id = #{id}")
    XCollectionTask getById(Long id);

    @Delete("delete from x_collection_task where id = #{id}")
    int delete(Long id);

    int update(XCollectionTask xCollectionTask);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_collection_task(order_id, repayment_gid, collector_id, collection_status, due_collection_amount, collection_amount, collection_overdue_fee, create_time, update_time, enable_flag) values(#{orderId}, #{repaymentGid}, #{collectorId}, #{collectionStatus}, #{dueCollectionAmount}, #{collectionAmount}, #{collectionOverdueFee}, #{createTime}, #{updateTime}, #{enableFlag})")
    int save(XCollectionTask xCollectionTask);

    int count(@Param("params") Map<String, Object> params);

    List<XCollectionTask> list(@Param("params") Map<String, Object> params,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);
}
