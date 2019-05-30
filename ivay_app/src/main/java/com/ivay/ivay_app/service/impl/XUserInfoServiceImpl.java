package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.advice.BusinessException;
import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.service.XLoanRateService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_app.service.XVirtualAccountService;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class XUserInfoServiceImpl implements XUserInfoService {

    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private I18nService i18nService;

    @Autowired
    private XUserInfoDao xUserInfoDao;

    @Autowired
    XVirtualAccountService xVirtualAccountService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public XUserInfo update(XUserInfo xUserInfo) {
        XUserInfo old = xUserInfoDao.getByGid(xUserInfo.getUserGid());
        if (old == null) {
            throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
        }
        if (!xUserInfo.getIdentityCard().equals(old.getIdentityCard())) {
            List<XUserInfo> list = xUserInfoDao.getByIdentityCard(xUserInfo.getIdentityCard());
            if (list.size() > 1 || (list.size() == 1 && !list.get(0).getUserGid().equals(xUserInfo.getUserGid()))) {
                throw new BusinessException(i18nService.getMessage("response.error.identityCard.repeat.code"),
                        i18nService.getMessage("response.error.identityCard.repeat.msg"));
            }
        }

        xUserInfo.setId(old.getId());
        xUserInfo.setPhone(old.getPhone());
        if (StringUtils.isEmpty(old.getUserStatus())) {
            xUserInfo.setUserStatus(SysVariable.USER_STATUS_REGISTRY);
        } else {
            xUserInfo.setUserStatus(old.getUserStatus());
        }
        xUserInfo.setAccountStatus(old.getAccountStatus());
        xUserInfo.setPassword(old.getPassword());
        xUserInfo.setCreditLine(old.getCreditLine());
        xUserInfo.setCanborrowAmount(old.getCanborrowAmount());
        xUserInfo.setTransPwd(old.getTransPwd());
        xUserInfo.setCreateTime(old.getCreateTime());
        xUserInfo.setUpdateTime(new Date());
        xUserInfo.setEnableFlag(old.getEnableFlag());

        return xUserInfoDao.update(xUserInfo) == 1 ? xUserInfo : null;
    }

    @Override
    public XUserInfo getByGid(String gid) {
        return xUserInfoDao.getByGid(gid);
    }

    @Override
    public void delete(String gid) {
        xUserInfoDao.delete(gid);
    }

    @Override
    public int submit(String gid) {
        return updateUserStatus(gid, SysVariable.USER_STATUS_CHECKING);
    }

    /**
     * 更新用户状态
     */
    @Override
    public int updateUserStatus(String gid, String status) {
        XUserInfo xUserInfo = xUserInfoDao.getByGid(gid);
        if (xUserInfo == null) {
            throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
        }
        xUserInfo.setUserStatus(status);
        xUserInfo.setUpdateTime(new Date());
        return xUserInfoDao.update(xUserInfo);
    }

    @Override
    public int approve(String gid, int flag) {
        return updateUserStatus(gid, flag > 0 ? SysVariable.USER_STATUS_AUTH_SUCCESS : SysVariable.USER_STATUS_AUTH_FAIL);
    }

    @Override
    public CreditLine getCreditLine(String gid) {
        return xUserInfoDao.getCreditLine(gid);
    }

    @Override
    public String getUserStatus(String gid) {
        return xUserInfoDao.getUserStatus(gid);
    }

    @Override
    public boolean hasTransPwd(String gid) {
        String hasTransPwd = xUserInfoDao.getTranPwd(gid);
        return StringUtils.isNotBlank(hasTransPwd);
    }


    @Override
    public void setTransPwd(String userGid, String transPwd) {
        transPwd = bCryptPasswordEncoder.encode(transPwd);
        xUserInfoDao.setTransPwd(userGid, transPwd);
    }

    @Override
    public PageTableResponse auditList(int limit, int num, XAuditCondition xAuditCondition) {
        PageTableRequest request = new PageTableRequest();
        request.setOffset(limit * (num - 1));
        request.setLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "create_time");
        params.put("userGid", xAuditCondition.getUserGid());
        params.put("name", xAuditCondition.getName());
        params.put("phone", xAuditCondition.getPhone());
        params.put("fromTime", xAuditCondition.getFromTime());
        params.put("toTime", xAuditCondition.getToTime());
        params.put("auditStatus", xAuditCondition.getAuditStatus());
        request.setParams(params);
        return new PageTableHandler((a) -> xUserInfoDao.auditCount(a.getParams()),
                (a) -> xUserInfoDao.auditList(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public XAuditDetail auditDetail(String userGid) {
        return xUserInfoDao.auditDetail(userGid);
    }

    @Resource
    private XLoanRateService xLoanRateService;

    @Override
    public int auditUpdate(String userGid, int flag) {
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        if (xUserInfo == null) {
            throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
        }
        switch (flag) {
            case 0:
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_FAIL);
                break;
            case 1:
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_SUCCESS);
                break;
            case 2:
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_RETRY);
                break;
        }
        xUserInfo.setUpdateTime(new Date());
        if (SysVariable.USER_STATUS_AUTH_SUCCESS.equals(xUserInfo.getUserStatus())) {
            xLoanRateService.initLoanRateAndCreditLimit(userGid);
        }
        return xUserInfoDao.update(xUserInfo);
    }

    @Override
    public VerifyCodeInfo checkIdentify(String gid, String idCard) {
        String identityCard = xUserInfoDao.getIdentityCardByGid(gid);
        if (idCard.equals(identityCard)) {
            //身份证校验通过，生成随机数返回
            String authCode = MsgAuthCode.getAuthNineCode();
            long effectiveTime = 120 * 1000;
            VerifyCodeInfo code = new VerifyCodeInfo();
            code.setCodeToken(authCode);
            code.setEffectiveTime(effectiveTime);
            redisTemplate.opsForValue().set(gid, authCode, effectiveTime, TimeUnit.MILLISECONDS);
            return code;
        }

        return null;

    }


}
