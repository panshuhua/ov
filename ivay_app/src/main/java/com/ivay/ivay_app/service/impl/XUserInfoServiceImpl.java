package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.service.ThreadPoolService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_app.service.XVirtualAccountService;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dto.CreditLine;
import com.ivay.ivay_repository.dto.VerifyCodeInfo;
import com.ivay.ivay_repository.model.XUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class XUserInfoServiceImpl implements XUserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(XUserInfoService.class);

    @Autowired
    private I18nService i18nService;

    @Autowired
    private XUserInfoDao xUserInfoDao;

    @Autowired
    XVirtualAccountService xVirtualAccountService;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ThreadPoolService threadPoolService;

    @Value("${auto_audit_url}")
    private String auto_audit_url;

    @Override
    public XUserInfo update(XUserInfo xUserInfo) {
        XUserInfo old = xUserInfoDao.getByGid(xUserInfo.getUserGid());
        if (old == null) {
            throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
        }
        //去除姓名前后的空格
        if (!StringUtils.isEmpty(xUserInfo.getName())) {
            String name = StringUtil.vietnameseToUpperEnglish(xUserInfo.getName());
            xUserInfo.setName(name);
        }
        xUserInfo.setIdentityCard(StringUtil.replaceBlank(xUserInfo.getIdentityCard()));
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
        // 修改本地数据库
        int flag = updateUserStatus(gid, SysVariable.USER_STATUS_CHECKING);
        if (flag == 1) {
            logger.info("调用manage服务进行自动审计...");
            threadPoolService.execute(() -> {
                Map<String, Object> params = new HashMap<>();
                params.put("userGid", gid);
                restTemplate.postForObject(auto_audit_url, null, Response.class, params);
            });
        }
        return flag;
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

    @Override
    public String checkMacCode(String macCode) {
        List<String> phones = xUserInfoDao.checkMacCode(macCode);
        if (phones.size() > 0) {
            return StringUtil.phoneDesensitization(phones.get(0));
        }
        return null;
    }
}
