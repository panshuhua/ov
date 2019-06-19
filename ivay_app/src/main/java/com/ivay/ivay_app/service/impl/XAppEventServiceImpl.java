package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.service.XAppEventService;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XAppEventDao;
import com.ivay.ivay_repository.model.XAppEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class XAppEventServiceImpl implements XAppEventService {
    private static final Logger logger = LoggerFactory.getLogger(XAppEventService.class);

    @Autowired
    private XAppEventDao xAppEventDao;

    @Override
    public XAppEvent save(XAppEvent xAppEvent) {
        XAppEvent old = xAppEventDao.getByGid(xAppEvent.getGid());
        Date now = new Date();
        xAppEvent.setUpdateTime(now);
        xAppEvent.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
        xAppEvent.setIsUpload(SysVariable.APP_UPLOAD_NO);
        if (old == null) {
            xAppEvent.setCreateTime(now);
            return xAppEventDao.save(xAppEvent) == 1 ? xAppEvent : null;
        } else {
            xAppEvent.setId(old.getId());
            xAppEvent.setCreateTime(old.getCreateTime());
            return xAppEventDao.update(xAppEvent) == 1 ? xAppEvent : null;
        }
    }

    @Override
    public List<XAppEvent> listToBeUpload(String userGid) {
        return xAppEventDao.listToBeUpload(userGid);
    }

    @Override
    public int delete(String gids) {
        if (StringUtils.isEmpty(gids)) {
            return 0;
        }
        return xAppEventDao.delete(gids.split(","));
    }
}
