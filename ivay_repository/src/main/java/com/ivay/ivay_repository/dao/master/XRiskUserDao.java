package com.ivay.ivay_repository.dao.master;


import com.ivay.ivay_repository.dto.RiskUserInfo;
import com.ivay.ivay_repository.dto.RiskUserResult;
import com.ivay.ivay_repository.model.RiskUser;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * descripiton:
 *
 * @author: xx
 * @date: 2019/4/31
 * @time: 21:43
 * @modifier:
 * @since:
 */
@Mapper
public interface XRiskUserDao {

    List<RiskUser> selectUserList();

    List<RiskUser> selectUserListByPhone(String phone);

    String selectPhoneByBatch(@Param("phones") String[] phones);

    /**
     * @Description 搜索白名单列表记录数
     * @Author Ryan
     * @Param [params]
     * @Return int
     * @Date 2019/7/23 9:40
     */
    int selectListCount(@Param("params") Map<String, Object> params);

    /**
     * @Description 搜索白名单列表
     * @Author Ryan
     * @Param [params, offset, limit]
     * @Return java.util.List<com.ivay.ivay_repository.dto.RiskUserInfo>
     * @Date 2019/7/23 9:41
     */
    List<RiskUserResult> selectRiskUserList(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * @Description 批量指派销售员
     * @Author Ryan
     * @Param [salesmanId, ids]
     * @Return int
     * @Date 2019/7/23 11:45
     */
    int updateSalesmanBatch(@Param("salesmanId") Integer salesmanId, @Param("ids") List<Integer> ids);

    /**
     * @Description 查询我的电销列表记录数
     * @Author Ryan
     * @Param [params]
     * @Return int
     * @Date 2019/7/23 14:25
     */
    int selectMySalesListCount(@Param("params") Map<String, Object> params);

    /**
     * @Description 查询我的电销列表
     * @Author Ryan
     * @Param [params, offset, limit]
     * @Return java.util.List<com.ivay.ivay_repository.dto.RiskUserResult>
     * @Date 2019/7/23 14:25
     */
    List<RiskUserResult> selectMySalesList(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * @Description 根据id查询
     * @Author Ryan
     * @Param [id]
     * @Return com.ivay.ivay_repository.model.RiskUser
     * @Date 2019/7/23 17:42
     */
    RiskUser getById(Integer id);

    /**
     * @Description 更新
     * @Author Ryan
     * @Param [riskUser]
     * @Return int
     * @Date 2019/7/23 18:20
     */
    int updateById(RiskUser riskUser);

    /**
     * @Description 查询所有没分配电销的白名单
     * @Author Ryan
     * @Param []
     * @Return java.util.List<com.ivay.ivay_repository.model.RiskUser>
     * @Date 2019/7/24 9:45
     */
    List<RiskUser> selectNotAssignedList();

    /**
     * @Description 批量更新
     * @Author Ryan
     * @Param [riskUserList]
     * @Return int
     * @Date 2019/7/24 11:18
     */
    int updateBatch(@Param("list") List<RiskUser> riskUserList);
}
