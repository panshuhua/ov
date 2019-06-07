package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.advice.BusinessException;
import com.ivay.ivay_manage.service.XConfigService;
import com.ivay.ivay_repository.dao.master.XConfigDao;
import com.ivay.ivay_repository.model.XConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class XConfigServiceImpl implements XConfigService {

    @Resource
    private XConfigDao xConfigDao;

    @Override
    public XConfig getByType(String type) {
        return xConfigDao.getByType(type);
    }

    @Override
    public String getContentByType(String type) {
        return xConfigDao.getByType(type).getContent();
    }

    @Override
    public XConfig update(XConfig xConfig) {
        if (StringUtils.isEmpty(xConfig.getType())) {
            throw new BusinessException("配置模板类型不能为空");
        }
        XConfig old = xConfigDao.getByType(xConfig.getType());
        int result;
        if (old == null) {
            result = xConfigDao.save(xConfig);
        } else {
            xConfig.setId(old.getId());
            result = xConfigDao.update(xConfig);
        }
        return result == 1 ? xConfig : null;
    }

    @Override
    public PageTableResponse list(int limit, int num) {
        PageTableRequest request = new PageTableRequest();
        request.setOffset(limit * (num - 1));
        request.setLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "id");
        request.setParams(params);
        return new PageTableHandler((a) -> xConfigDao.count(a.getParams()),
                (a) -> xConfigDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }
}
