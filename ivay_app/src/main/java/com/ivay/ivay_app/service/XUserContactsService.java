package com.ivay.ivay_app.service;

import com.ivay.ivay_app.model.RiskInfo;
import com.ivay.ivay_common.table.PageTableResponse;

public interface XUserContactsService {

    /**
     * 根据gid添加联系人，gps，社交类app的个数
     *
     * @param riskInfo
     * @return
     */
    boolean saveAll(RiskInfo riskInfo);

    PageTableResponse getByGid(Integer limit, Integer num, String gid);
}
