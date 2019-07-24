package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.dto.RiskUserInfo;
import com.ivay.ivay_repository.model.RiskUser;

import java.util.List;

public interface RiskUserService {

    List<RiskUser> listUser();

    List<RiskUser> selectUserListByPhone(String phone);

    /**
     * @Description 搜索白名单列表
     * @Author Ryan
     * @Param [limit, num, riskUserInfo]
     * @Return com.ivay.ivay_common.table.PageTableResponse
     * @Date 2019/7/23 9:22
     */
    PageTableResponse list(int limit, int num, RiskUserInfo riskUserInfo);

    /**
     * @Description 指派销售员
     * @Author Ryan
     * @Param [salesmanId, list]
     * @Return java.lang.Boolean
     * @Date 2019/7/23 11:24
     */
    Boolean updateSalesman(Integer salesmanId, List<Integer> list);

    /**
     * @Description 我的电销列表
     * @Author Ryan
     * @Param [limit, num, riskUserInfo]
     * @Return com.ivay.ivay_common.table.PageTableResponse
     * @Date 2019/7/23 14:03
     */
    PageTableResponse mySalesList(int limit, int num, RiskUserInfo riskUserInfo);

    /**
     * @Description 触发自动分配电销接口
     * @Author Ryan
     * @Param []
     * @Return java.lang.Boolean
     * @Date 2019/7/24 9:41
     */
    Boolean updateSalesmanAuto();
}