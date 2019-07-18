package com.ivay.ivay_app.service;

import com.ivay.ivay_repository.model.XUserExtInfo;

public interface XUserExtInfoService {
    /**
     * 保存用户信息
     *
     * @param xUserExtInfo
     */
    XUserExtInfo save(XUserExtInfo xUserExtInfo);

    /**
     * 编辑用户信息
     *
     * @param xUserExtInfo
     */
    XUserExtInfo update(XUserExtInfo xUserExtInfo);

    /**
     * 根据gid删除
     *
     * @param gid
     * @return
     */
    void delete(String gid);
}
