package com.ivay.ivay_app.service;

import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.model.XLoanRate;

import java.util.List;

public interface XLoanRateService {
    /**
     * 手动设置借款利率
     *
     * @param xLoanRate
     * @return
     */
    int save(XLoanRate xLoanRate);

    /**
     * 手动批量插入借款利率
     *
     * @param list
     * @return
     */
    int saveByBatch(List<XLoanRate> list);

    /**
     * 获取借款利率列表
     *
     * @param limit
     * @param num
     * @param userGid
     * @return
     */
    PageTableResponse list(int limit, int num, String userGid);

    /**
     * 根据后台配置某个用户的借款利率
     *
     * @param userGid
     */
    int acquireLoanRate(String userGid);
}
