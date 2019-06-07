package com.ivay.ivay_manage.service;

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

    void save(Long userId, String module, Boolean flag, String remark);

    void deleteLogs();
}
