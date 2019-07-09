package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_manage.service.XUserExtInfoService;
import com.ivay.ivay_repository.dao.master.XUserExtInfoDao;
import com.ivay.ivay_repository.model.XUserExtInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XUserExtInfoServiceImpl implements XUserExtInfoService {

    @Autowired
    private XUserExtInfoDao xUserExtInfoDao;

    @Override
    public XUserExtInfo getByGid(String gid) {
        return xUserExtInfoDao.getByGid(gid);
    }
}
