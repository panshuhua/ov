package com.ivay.ivay_repository.dao.risk;


import com.ivay.ivay_repository.dto.RiskUserInfo;
import com.ivay.ivay_repository.model.RiskUser;
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
public interface RiskUserDao {

    List<RiskUser> selectUserList();

    List<RiskUser> selectUserListByPhone(String phone);

    String selectPhoneByBatch(@Param("phones") String[] phones);

}
