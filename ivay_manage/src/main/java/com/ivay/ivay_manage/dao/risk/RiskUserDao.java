package com.ivay.ivay_manage.dao.risk;


import com.ivay.ivay_manage.model.RiskUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
public interface RiskUserDao {

    List<RiskUser> selectUserList();
    
    //List<RiskUser> selectUserListByUserId(Integer userId);
    
    List<RiskUser> selectUserListByPhone(String phone);

    //void update(RiskUser user);
}
