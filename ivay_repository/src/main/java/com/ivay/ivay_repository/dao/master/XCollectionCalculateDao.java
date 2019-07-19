package com.ivay.ivay_repository.dao.master;

import com.ivay.ivay_repository.dto.CollectionCalculateResult;
import com.ivay.ivay_repository.dto.CollectionRepayDetail;
import com.ivay.ivay_repository.model.XCollectionCalculate;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface XCollectionCalculateDao {

    @Select("select * from x_collection_calculate t where t.id = #{id}")
    XCollectionCalculate getById(Integer id);

    @Delete("delete from x_collection_calculate where id = #{id}")
    int delete(Integer id);

    int update(XCollectionCalculate xCollectionCalculate);
    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into x_collection_calculate(calculate_date, overdue_order, overdue_user, overdue_principal, amount_receivable, number_repay, amount_repay, create_time, update_time) values(#{calculateDate}, #{overdueOrder}, #{overdueUser}, #{overduePrincipal}, #{amountReceivable}, #{numberRepay}, #{amountRepay}, #{createTime}, #{updateTime})")
    int save(XCollectionCalculate xCollectionCalculate);
    
    int count(@Param("params") Map<String, Object> params);

    List<XCollectionCalculate> list(@Param("params") Map<String, Object> params,
                          @Param("offset") Integer offset,
                          @Param("limit") Integer limit);

    /**
     * @Description 获取催收报表的逾期订单统计
     * @Author Ryan
     * @Param [beginDate, endDate]
     * @Return com.ivay.ivay_repository.model.XCollectionCalculate
     * @Date 2019/7/17 13:37
     */
    XCollectionCalculate selectCollectionsCalculate(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * @Description 获取催收报表的还款统计
     * @Author Ryan
     * @Param [beginDate, endDate]
     * @Return com.ivay.ivay_repository.model.XCollectionCalculate
     * @Date 2019/7/17 13:38
     */
    XCollectionCalculate selectRepaytionCalculate(Date beginDate, Date endDate);

    /**
     * @Description 查询催收统计列表
     * @Author Ryan
     * @Param [params, offset, limit]
     * @Return java.util.List<com.ivay.ivay_repository.model.XCollectionCalculate>
     * @Date 2019/7/17 15:17
     */
    List<XCollectionCalculate> selectCalculateList(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * @Description 查询催收统计列表记录总数
     * @Author Ryan
     * @Param [params]
     * @Return int
     * @Date 2019/7/17 15:18
     */
    int selectCalculateListCount(@Param("params") Map<String, Object> params);

    /**
     * @Description 查询催收统计总数
     * @Author Ryan
     * @Param [params]
     * @Return com.ivay.ivay_repository.dto.CollectionCalculateResult
     * @Date 2019/7/17 15:42
     */
    CollectionCalculateResult selectTotalCalculate(@Param("params") Map<String, Object> params);

    /**
     * @Description 还款名单
     * @Author Ryan
     * @Param [params, offset, limit]
     * @Return java.util.List<com.ivay.ivay_repository.dto.CollectionRepayDetail>
     * @Date 2019/7/17 19:24
     */
    List<CollectionRepayDetail> selectRepayList(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * @Description 还款名单记录数
     * @Author Ryan
     * @Param [params]
     * @Return int
     * @Date 2019/7/17 20:34
     */
    int selectRepayListCount(@Param("params") Map<String, Object> params);

    /**
     * @Description 根据统计时间查询对象
     * @Author Ryan
     * @Param [date]
     * @Return com.ivay.ivay_repository.model.XCollectionCalculate
     * @Date 2019/7/18 10:06
     */
    XCollectionCalculate getByCalculateTime(@Param("date") Date date);

    /**
     * @Description 催收报表EXCEL导出数据
     * @Author Ryan
     * @Param [beginTime, endTime]
     * @Return java.util.List<com.ivay.ivay_repository.model.XCollectionCalculate>
     * @Date 2019/7/19 9:15
     */
    List<XCollectionCalculate> selectExcelList(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
}
