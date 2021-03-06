package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.model.XCollectionRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XCollectionRecordDao {

    @Select("select * from x_collection_record t where t.id = #{id}")
    XCollectionRecord getById(Long id);

    @Delete("delete from x_collection_record where id = #{id}")
    int delete(Long id);

    int update(XCollectionRecord xCollectionRecord);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_collection_record(task_id, order_id, collector_id, collection_phone, collection_reason, collection_time, remark, create_time, update_time, enable_flag) values(#{taskId}, #{orderId}, #{collectorId}, #{collectionPhone}, #{collectionReason}, #{collectionTime}, #{remark}, #{createTime}, #{updateTime}, #{enableFlag})")
    int save(XCollectionRecord xCollectionRecord);

    int count(@Param("params") Map<String, Object> params);

    List<XCollectionRecord> list(@Param("params") Map<String, Object> params,
                                 @Param("offset") Integer offset,
                                 @Param("limit") Integer limit);

    int selectCollectionCount(@Param("params") Map<String, Object> params);

    List<XCollectionRecord> selectCollectionRecordList(@Param("params")  Map<String, Object> params,
                                                       @Param("offset") Integer offset,
                                                       @Param("limit") Integer limit);
}
