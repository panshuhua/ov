package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.dto.CollectionTaskResult;
import com.ivay.ivay_repository.model.XCollectionTask;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface XCollectionTaskDao {

    @Select("select * from x_collection_task t where t.id = #{id}")
    XCollectionTask getById(Integer id);

    @Delete("delete from x_collection_task where id = #{id}")
    int delete(Long id);

    int update(XCollectionTask xCollectionTask);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_collection_task(order_id, user_gid, collector_id, collection_status, due_collection_amount, collection_amount, collection_overdue_fee, create_time, update_time, enable_flag,collection_repay_status,repay_time) values(#{orderId}, #{userGid}, #{collectorId}, #{collectionStatus}, #{dueCollectionAmount}, #{collectionAmount}, #{collectionOverdueFee}, #{createTime}, #{updateTime}, #{enableFlag}),#{collectionRepayStatus},#{repayTime}")
    int save(XCollectionTask xCollectionTask);

    int count(@Param("params") Map<String, Object> params);

    List<XCollectionTask> list(@Param("params") Map<String, Object> params,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    /**
     * @Description 批量插入催收档案（定时任务）
     * @Author Ryan
     * @Param [collectionTaskList]
     * @Return int
     * @Date 2019/7/9 14:17
     */
    int saveBatch(List<XCollectionTask> collectionTaskList);

    /**
     * @Description 查询催收订单中的所有订单号
     * @Author Ryan
     * @Param []
     * @Return java.util.List<java.lang.String>
     * @Date 2019/7/9 15:01
     */
    List<String> selectOrderIds();

    /**
     * @Description 根据搜索条件查找催收列表
     * @Author Ryan
     * @Param [params, offset, limit]
     * @Return java.util.List<CollectionTaskResult>
     * @Date 2019/7/9 16:43
     */
    List<CollectionTaskResult> listByParams(@Param("params") Map<String, Object> params,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);

    /**
     * @Description 获取列表查询总数
     * @Author Ryan
     * @Param [params]
     * @Return int
     * @Date 2019/7/10 10:25
     */
    int selectParamsListCount(@Param("params") Map<String, Object> params);

    /**
     * @Description 根据用户gid获取最新的催收任务
     * @Author Ryan
     * @Param [userGid]
     * @Return com.ivay.ivay_repository.model.XCollectionTask
     * @Date 2019/7/11 11:35
     */
    XCollectionTask findNewCollectionByUserGid(@Param("userGid") String userGid);

    /**
     * @Description 獲取我的催收記錄數量
     * @Author Ryan
     * @Param [params]
     * @Return int
     * @Date 2019/7/12 9:41
     */
    int getCollectionListByUserGidCount(@Param("params") Map<String, Object> params);

    /**
     * @Description 查詢我的催收
     * @Author Ryan
     * @Param [params, offset, limit]
     * @Return java.util.List<com.ivay.ivay_repository.dto.CollectionTaskResult>
     * @Date 2019/7/12 9:42
     */
    List<CollectionTaskResult> getCollectionListByUserGid(@Param("params") Map<String, Object> params,
                                                          @Param("offset") Integer offset,
                                                          @Param("limit") Integer limit);
}
