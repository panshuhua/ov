package com.ivay.ivay_manage.service;

import com.ivay.ivay_repository.model.RiskUser;

import java.util.List;

public interface RiskUserService {

    List<RiskUser> listUser();

    List<RiskUser> selectUserListByPhone(String phone);

}