package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XConfig;

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
     * 配置列表
     *
     * @param limit
     * @param num
     * @return
     */
    PageTableResponse list(int limit, int num);
}
