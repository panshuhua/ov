package com.ivay.ivay_manage.service;

import com.ivay.ivay_manage.model.XConfig;
import com.ivay.ivay_manage.table.PageTableResponse;

public interface XConfigService {
    /**
     * 获取模板
     *
     * @param type
     * @return
     */
    XConfig getByType(String type);

    /**
     * 获取模板得配置内容
     *
     * @param type
     * @return
     */
    String getContentByType(String type);

    /**
     * 更新配置
     *
     * @param xConfig
     * @return
     */
    XConfig update(XConfig xConfig);

    /**
     * 配置列表
     *
     * @param limit
     * @param num
     * @return
     */
    PageTableResponse list(int limit, int num);
}
