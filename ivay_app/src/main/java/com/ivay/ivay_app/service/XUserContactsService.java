package com.ivay.ivay_app.service;

import javax.servlet.http.HttpServletRequest;

import com.ivay.ivay_repository.dto.XRiskInfo;

public interface XUserContactsService {

    /**
     * 根据gid添加联系人，gps，社交类app的个数
     *
     * @param riskInfo
     * @return
     */
    boolean saveAll(String type, XRiskInfo riskInfo, HttpServletRequest request) throws Exception;

}
