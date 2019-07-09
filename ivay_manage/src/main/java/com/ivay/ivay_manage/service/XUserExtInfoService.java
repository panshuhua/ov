package com.ivay.ivay_manage.service;

import com.ivay.ivay_repository.model.XUserExtInfo;

public interface XUserExtInfoService {
    /**
     * 根据id获取信息
     *
     * @param gid
     * @return
     */
    XUserExtInfo getByGid(String gid);
}
