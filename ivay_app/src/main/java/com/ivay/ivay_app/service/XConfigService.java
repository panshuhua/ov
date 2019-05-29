package com.ivay.ivay_app.service;

import com.ivay.ivay_app.model.XConfig;
import com.ivay.ivay_common.table.PageTableResponse;

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
     * 获取模板
     *
     * @param type
     * @param lang
     * @return
     */
    XConfig getByType(String type, String lang);

    /**
     * 获取模板得配置内容
     *
     * @param type
     * @param lang
     * @return
     */
    String getContentByType(String type, String lang);

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
