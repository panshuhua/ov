package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.BlackUser;

public interface BlackUserService {
    BlackUser save(BlackUser blackUser);

    BlackUser get(Long id);

    BlackUser update(BlackUser blackUser);

    PageTableResponse list(PageTableRequest request);

    int delete(Long id);

    /**
     * 根据电话号码 或身份证判断是否是黑名单用户
     *
     * @param phone
     * @param identityCard
     * @return
     */
    boolean isBlackUser(String phone, String identityCard);
}
