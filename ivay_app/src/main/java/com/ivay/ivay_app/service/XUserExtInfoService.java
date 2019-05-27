package com.ivay.ivay_app.service;

import com.ivay.ivay_app.model.XUserExtInfo;

public interface XUserExtInfoService {
    /**
     * 保存用户信息
     * @param xUserExtInfo
     */
    XUserExtInfo save(XUserExtInfo xUserExtInfo);

    /**
     * 根据id获取信息
     * @param gid
     * @return
     */
    XUserExtInfo getByGid(String gid);

    /**
     * 编辑用户信息
     * @param xUserExtInfo
     */
    XUserExtInfo update(XUserExtInfo xUserExtInfo);

    /**
     * 根据gid删除
     * @param gid
     * @return
     */
    void delete(String gid);
}
