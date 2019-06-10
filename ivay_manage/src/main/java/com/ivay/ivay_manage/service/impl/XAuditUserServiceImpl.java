package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.service.XAuditUserService;
import com.ivay.ivay_repository.dao.master.XAuditUserDao;
import com.ivay.ivay_repository.model.XAuditUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class XAuditUserServiceImpl implements XAuditUserService {
    @Autowired
    private XAuditUserDao xAuditUserDao;

    @Override
    public XAuditUser getById(Long id) {
        return xAuditUserDao.getById(id);
    }

    @Override
    public int delete(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return 0;
        }
        return xAuditUserDao.deleteByBatch(ids.split(","));
    }

    @Override
    public XAuditUser update(String auditId, String userGid) {
        // 查出所有的审计员
        List<String> auditIds = xAuditUserDao.getSysUserByRole(SysVariable.ROLE_OVAY_AUDIT);
        if (auditIds.isEmpty() || !auditIds.contains(auditId)) {
            throw new BusinessException("审计员不存在，请先增加");
        }

        // 若不指定审计员则随机分配
        if (StringUtils.isEmpty(auditId)) {
            auditId = auditIds.get((int) (1 + Math.random() * (auditIds.size())));
        }
        // 查询用户是否已分配
        XAuditUser xAuditUser = xAuditUserDao.getByUserGid(userGid);
        Date now = new Date();
        if (xAuditUser == null) {
            xAuditUser = new XAuditUser();
            xAuditUser.setCreateTime(now);
            xAuditUser.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
            xAuditUser.setUserGid(userGid);
        }
        xAuditUser.setSysUserId(auditId);
        xAuditUser.setUpdateTime(now);
        if (xAuditUser.getId() == 0) {
            xAuditUserDao.save(xAuditUser);
        } else {
            xAuditUserDao.update(xAuditUser);
        }
        return xAuditUser;
    }

    @Override
    public XAuditUser save(XAuditUser xAuditUser) {
        xAuditUserDao.save(xAuditUser);
        return xAuditUser;
    }

    @Override
    public List<XAuditUser> getBySysUserId(String sysUserId) {
        return xAuditUserDao.getBySysUserId(sysUserId);
    }

    @Override
    public PageTableResponse list(PageTableRequest request) {
        Map param = request.getParams();
        param.put("type", SysVariable.ROLE_OVAY_AUDIT);
        request.setParams(param);
        return new PageTableHandler((a) -> xAuditUserDao.count(a.getParams()),
                (b) -> xAuditUserDao.list(b.getParams(), b.getOffset(), b.getLimit())
        ).handle(request);
    }
}
