package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.advice.BusinessException;
import com.ivay.ivay_app.dao.XConfigDao;
import com.ivay.ivay_app.model.XConfig;
import com.ivay.ivay_app.service.XConfigService;
import com.ivay.ivay_app.table.PageTableHandler;
import com.ivay.ivay_app.table.PageTableRequest;
import com.ivay.ivay_app.table.PageTableResponse;
import com.ivay.ivay_app.utils.LocaleUtils;
import com.ivay.ivay_app.utils.SysVariable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class XConfigServiceImpl implements XConfigService {

    @Autowired
    private XConfigDao xConfigDao;

    @Override
    public XConfig getByType(String type) {
        if (SysVariable.TEMPLATE_EDUCATION.equals(type) ||
                SysVariable.TEMPLATE_MARITAL.equals(type) ||
                SysVariable.TEMPLATE_RELATION.equals(type) ||
                SysVariable.TEMPLATE_SEX.equals(type) ||
                SysVariable.TEMPLATE_AUDIT_STATUS.equals(type)) {
            return xConfigDao.getByTypeAndLang(type, LocaleUtils.getContextLocale().getLanguage());
        }
        return xConfigDao.getByType(type);
    }

    @Override
    public String getContentByType(String type) {
        return getByType(type).getContent();
    }

    @Override
    public XConfig getByType(String type, String lang) {
        return xConfigDao.getByTypeAndLang(type, lang);
    }

    @Override
    public String getContentByType(String type, String lang) {
        return xConfigDao.getByTypeAndLang(type, lang).getContent();
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
