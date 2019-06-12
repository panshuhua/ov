package com.ivay.ivay_app.service;

import com.ivay.ivay_repository.model.SysLogs;

/**
 * 日志service
 *
 * @author xx
 * <p>
 * 2017年8月19日
 */
public interface SysLogService {

    void save(SysLogs sysLogs);

    void save(String userGid,String phone,String module, Boolean flag, String remark);

    void deleteLogs();
}
