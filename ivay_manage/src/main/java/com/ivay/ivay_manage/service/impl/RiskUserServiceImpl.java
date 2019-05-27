package com.ivay.ivay_manage.service.impl;


import com.ivay.ivay_manage.dao.risk.RiskUserDao;
import com.ivay.ivay_manage.model.RiskUser;
import com.ivay.ivay_manage.service.RiskUserService;
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

//    @Override
//    public List<RiskUser> selectUserListByUserId(Integer userId){
//    	return riskUserDao.selectUserListByUserId(userId);
//    }
//    
//    @Override
//    @Transactional(value = "slaveTransactionManager")
//    public void update(RiskUser user) {
//    	riskUserDao.update(user);
//        //int i = 10 / 0;
//    }

	@Override
	public List<RiskUser> selectUserListByPhone(String phone) {
		// TODO Auto-generated method stub		
		return riskUserDao.selectUserListByPhone(phone);
	}
}