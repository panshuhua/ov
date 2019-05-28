package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.advice.BusinessException;
import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.service.XUserExtInfoService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XUserExtInfoDao;
import com.ivay.ivay_repository.model.XUserExtInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class XUserExtInfoServiceImpl implements XUserExtInfoService {

    @Autowired
    private XUserExtInfoDao xUserExtInfoDao;

    @Autowired
    private XUserInfoService xUserInfoService;

    @Autowired
    private I18nService i18nService;

    @Override
    public XUserExtInfo save(XUserExtInfo xUserExtInfo) {
        xUserExtInfo.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
        xUserExtInfo.setCreateTime(new Date());
        xUserExtInfo.setUpdateTime(new Date());
        return xUserExtInfoDao.save(xUserExtInfo) == 1 ? xUserExtInfo : null;
    }

    @Override
    public XUserExtInfo getByGid(String gid) {
        return xUserExtInfoDao.getByGid(gid);
    }

    @Override
    public XUserExtInfo update(XUserExtInfo xUserExtInfo) {
        if (StringUtils.isEmpty(xUserExtInfo.getUserGid())) {
            throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
        }

        XUserExtInfo old = xUserExtInfoDao.getByGid(xUserExtInfo.getUserGid());
        if (old == null) {
            xUserExtInfo.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
            Date now = new Date();
            xUserExtInfo.setCreateTime(now);
            xUserExtInfo.setUpdateTime(now);
            return xUserExtInfoDao.save(xUserExtInfo) == 1 ? xUserExtInfo : null;
        } else {
            xUserExtInfo.setEnableFlag(old.getEnableFlag());
            xUserExtInfo.setId(old.getId());
            xUserExtInfo.setCreateTime(old.getCreateTime());
            xUserExtInfo.setUpdateTime(new Date());
            return xUserExtInfoDao.update(xUserExtInfo) == 1 ? xUserExtInfo : null;
        }
    }

    @Override
    public void delete(String gid) {
        xUserExtInfoDao.delete(gid);
    }
}
