package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.service.XAuditService;
import com.ivay.ivay_manage.service.XUserInfoService;
import com.ivay.ivay_repository.dao.master.UserDao;
import com.ivay.ivay_repository.dao.master.XAuditUserDao;
import com.ivay.ivay_repository.model.XAuditUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class XAuditServiceImpl implements XAuditService {
    private static final Logger logger = LoggerFactory.getLogger(XAuditService.class);

    @Autowired
    private XAuditUserDao xAuditUserDao;

    @Autowired
    private XUserInfoService xUserInfoService;

    @Override
    public XAuditUser save(XAuditUser xAuditUser) {
        xAuditUserDao.save(xAuditUser);
        return xAuditUser;
    }

    @Override
    public XAuditUser getById(Long id) {
        return xAuditUserDao.getById(id);
    }

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAudit(Long id) {
        userDao.delete(id);
        userDao.deleteUserRole(id);
        return xAuditUserDao.deleteAudit(id);
    }

    @Override
    public int deleteUser(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return 0;
        }
        return xAuditUserDao.deleteUser(ids.split(","));
    }

    @Override
    public XAuditUser assignAuditForUser(String auditId, String userGid) {
        // 查出所有的审计员
        List<String> auditIds = xAuditUserDao.getSysUserByRole(SysVariable.ROLE_OVAY_AUDIT);
        if (auditIds.isEmpty()) {
            logger.info("不存在审计员");
            return null;
        } else if (StringUtils.isEmpty(auditId)) {
            // 若不指定审计员则随机分配
            auditId = auditIds.get((int) (Math.random() * (auditIds.size())));
        } else if (!auditIds.contains(auditId)) {
            throw new BusinessException("审计员({})不存在", auditId);
        }

        // 查询用户是否已分配
        XAuditUser xAuditUser = xAuditUserDao.getByUserGid(userGid);
        Date now = new Date();
        if (xAuditUser == null) {
            xAuditUser = new XAuditUser();
            xAuditUser.setCreateTime(now);
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

    /**
     * 将某审计员的审核人员重新分配
     *
     * @param acceptId
     * @param handleId
     * @return
     */
    @Override
    public boolean reAssignAudit(String acceptId, String handleId) {
        if (StringUtils.isEmpty(handleId)) {
            return false;
        } else if (handleId.equals(acceptId)) {
            return true;
        }
        if (StringUtils.isEmpty(acceptId)) {
            // 查出所有的审计员
            List<String> auditIds = xAuditUserDao.getSysUserByRole(SysVariable.ROLE_OVAY_AUDIT);
            if (auditIds.size() == 0) {
                return xAuditUserDao.deleteAll() == 0;
            }
            acceptId = auditIds.get((int) (Math.random() * (auditIds.size())));
            logger.info("随机分配审计员id：{}", acceptId);
        }
        xAuditUserDao.reAssignAudit(acceptId, handleId);
        return true;
    }

    @Override
    public List<XAuditUser> getAuditBySysUserId(String sysUserId) {
        return xAuditUserDao.getBySysUserId(sysUserId);
    }

    @Override
    public PageTableResponse listAudit(PageTableRequest request) {
        Map<String, Object> param = request.getParams();
        // 查询审计员
        param.put("role", SysVariable.ROLE_OVAY_AUDIT);
        request.setParams(param);
        return new PageTableHandler(
                a -> xAuditUserDao.count(a.getParams()),
                b -> xAuditUserDao.list(b.getParams(), b.getOffset(), b.getLimit())
        ).handle(request);
    }

    @Override
    public PageTableResponse listUser(PageTableRequest request) {
        return xUserInfoService.auditList(request);
    }
}
