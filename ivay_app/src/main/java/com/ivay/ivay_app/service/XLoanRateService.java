package com.ivay.ivay_app.service;

import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XLoanRate;

public interface XLoanRateService {
    /**
     * 手动设置借款利率
     *
     * @param xLoanRate
     * @return
     */
    int save(XLoanRate xLoanRate);

    /**
     * 获取借款利率列表
     *
     * @param limit
     * @param num
     * @param userGid
     * @return
     */
    PageTableResponse list(int limit, int num, String userGid);
}
