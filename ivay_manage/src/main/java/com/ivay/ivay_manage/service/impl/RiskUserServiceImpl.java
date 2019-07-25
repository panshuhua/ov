package com.ivay.ivay_manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.RiskUserService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.UserDao;
import com.ivay.ivay_repository.dao.master.XRiskUserDao;
import com.ivay.ivay_repository.dto.RiskUserInfo;
import com.ivay.ivay_repository.dto.RiskUserResult;
import com.ivay.ivay_repository.dto.UserName;
import com.ivay.ivay_repository.model.RiskUser;
import com.ivay.ivay_repository.model.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * descripiton:
 *
 * @author: xx风控用户服务
 * @date: 2019/5/20
 * @time: 21:56
 * @modifier:
 * @since:
 */
@Service
public class RiskUserServiceImpl implements RiskUserService {

    private static final Logger logger = LoggerFactory.getLogger(RiskUserServiceImpl.class);

    @Autowired
    private XRiskUserDao riskUserDao;
    @Autowired
    private UserDao userDao;

    @Override
    public List<RiskUser> listUser() {
        return riskUserDao.selectUserList();
    }

    @Override
    public List<RiskUser> selectUserListByPhone(String phone) {
        return riskUserDao.selectUserListByPhone(phone);
    }

    @Override
    public PageTableResponse list(int limit, int num, RiskUserInfo riskUserInfo) {
        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);

        Map params = request.getParams();
        if (null != riskUserInfo) {
            params.put("name", riskUserInfo.getName());
            params.put("phone", riskUserInfo.getPhone());
            //如果用户是冻结状态
            if (riskUserInfo.getUserStatus() == 8) {
                params.put("account_status", 1);
            }else {
                params.put("userStatus", riskUserInfo.getUserStatus());
            }
            params.put("userId", riskUserInfo.getUserId());
            params.put("assignStatus", riskUserInfo.getAssignStatus());
            params.put("username", riskUserInfo.getUsername());
            params.put("importTimeStart", riskUserInfo.getImportTimeStart());
            params.put("importTimeEnd", riskUserInfo.getImportTimeEnd());
        }
        List<RiskUserResult> riskUserResultList = riskUserDao.selectRiskUserList(request.getParams(), request.getOffset(), request.getLimit());
        riskUserResultList.forEach(o -> {
            if (o.getAccountStatus() !=null &&o.getAccountStatus() == 1) {
                o.setUserStatus(8);
            }
        });

        return new PageTableHandler(
                a -> riskUserDao.selectListCount(a.getParams()),
                a -> riskUserResultList
        ).handle(request);
    }

    @Override
    public Boolean updateSalesman(Integer salesmanId, List<Integer> ids) {
        logger.info("指派白名单销售员{}，销售员id{}", JSONObject.toJSONString(ids), salesmanId);

        //查询销售员是否存在
        SysUser sysUser = userDao.getById(Long.valueOf(salesmanId));
        if (null == sysUser) {
            throw new BusinessException("销售员不存在，请刷新页面重试！");
        }

        int n = riskUserDao.updateSalesmanBatch(salesmanId, ids);
        if (n > 0){
            return true;
        }
        return false;
    }

    @Override
    public PageTableResponse mySalesList(int limit, int num, RiskUserInfo riskUserInfo) {
        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);

        Map params = request.getParams();
        Long id = UserUtil.getLoginUser().getId();
        params.put("salesmanId", id.intValue());

        if (null != riskUserInfo) {
            params.put("name", riskUserInfo.getName());
            params.put("phone", riskUserInfo.getPhone());
            //如果用户是冻结状态
            if (riskUserInfo.getUserStatus() == 8) {
                params.put("account_status", 1);
            }else {
                params.put("userStatus", riskUserInfo.getUserStatus());
            }
            params.put("dealStatus", riskUserInfo.getDealStatus());
            params.put("assignTimeStart", riskUserInfo.getAssignTimeStart());
            params.put("assignTimeEnd", riskUserInfo.getAssignTimeEnd());
        }

        List<RiskUserResult> resultList = riskUserDao.selectMySalesList(request.getParams(), request.getOffset(), request.getLimit());
        resultList.forEach(o -> {
            if (o.getAccountStatus() != null && o.getAccountStatus() == 1) {
                o.setUserStatus(8);
            }
        });

        return new PageTableHandler(
                a -> riskUserDao.selectMySalesListCount(a.getParams()),
                a -> resultList
        ).handle(request);
    }

    @Override
    public Boolean updateSalesmanAuto() {
        // 查询所有没分配电销的白名单
        List<RiskUser> riskUserList = riskUserDao.selectNotAssignedList();

        if (null == riskUserList || riskUserList.size() == 0){
            throw new BusinessException("没有需要自动指派的名单");
        }
        // 查询所有的电销销售员
        List<UserName> userNameList = userDao.getSalesNames("salesAdmin");

        if (null == userNameList || userNameList.size() == 0){
            throw new BusinessException("没有销售员可以指派，请先指定销售角色");
        }

        Date assignTime = new Date();
        //将白名单平均指派给销售员
        for (RiskUser riskUser : riskUserList) {
            int salesNum = userNameList.size();
            riskUser.setSalesmanId(userNameList.get(riskUserList.size()%salesNum).getId());
            riskUser.setAssignTime(assignTime);
            riskUser.setAssignStatus(1);
            riskUser.setDealStatus(0);
        }

        riskUserDao.updateBatch(riskUserList);

        return false;
    }
}