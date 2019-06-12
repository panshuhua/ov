package com.ivay.ivay_manage.service.impl;


import com.ivay.ivay_manage.service.RiskUserService;
import com.ivay.ivay_repository.dao.risk.RiskUserDao;
import com.ivay.ivay_repository.model.RiskUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    private RiskUserDao riskUserDao;

    @Override
    public List<RiskUser> listUser() {
        return riskUserDao.selectUserList();
    }

    @Override
    public List<RiskUser> selectUserListByPhone(String phone) {
        return riskUserDao.selectUserListByPhone(phone);
    }
}