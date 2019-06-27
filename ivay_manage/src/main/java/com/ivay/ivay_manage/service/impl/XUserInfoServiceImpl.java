package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.service.*;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dto.XAuditDetail;
import com.ivay.ivay_repository.model.RiskUser;
import com.ivay.ivay_repository.model.XAppEvent;
import com.ivay.ivay_repository.model.XUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;


@Service
public class XUserInfoServiceImpl implements XUserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(XUserInfoService.class);

    @Resource
    private XUserInfoDao xUserInfoDao;
    @Resource
    private XLoanService xLoanService;

    @Autowired
    private XConfigService xConfigService;

    @Autowired
    private XUserInfoService xUserInfoService;

    @Autowired
    private RiskUserService riskUserService;

    @Autowired
    private XAuditService xAuditService;

    @Autowired
    private I18nService i18nService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ThreadPoolService threadPoolService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${xAppEvents_event_url}")
    private String xAppEvents_event_url;

    @Override
    public PageTableResponse auditList(PageTableRequest request) {
        Object time = request.getParams().get("toTime");
        if (time != null) {
            if (!StringUtils.isEmpty(time.toString())) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtils.stringToDate_YYYY_MM_DD(time.toString()));
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                request.getParams().put("toTime", DateUtils.dateToString_YYYY_MM_DD_HH_MM_SS(calendar.getTime()));
            }
        }

        // 设置角色与登录用户id
        request.getParams().put("role", roleService.getLoginUserAuditRole());
        request.getParams().put("loginId", UserUtil.getLoginUser().getId());

        return new PageTableHandler(
                a -> xUserInfoDao.auditCount(a.getParams()),
                a -> xUserInfoDao.auditList(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public XAuditDetail auditDetail(String userGid) {
        return xUserInfoDao.auditDetail(userGid);
    }

    /**
     * 对待授信用户进行人工审核
     *
     * @param userGid
     * @param flag       0 审核拒绝 1 审核通过
     * @param refuseCode 审核拒绝时，必须传入refuseCode
     * @param refuseDemo
     * @param type       审核类型 0人工 1自动
     * @return 1审核通过 0 审核拒绝 -1数据库异常
     */
    @Override
    public int auditUpdate(String userGid, int flag, String refuseCode, String refuseDemo, String type) {
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        if (xUserInfo == null) {
            throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
        }

        // region -- 审核待授信用户
        Date now = new Date();
        xUserInfo.setUpdateTime(now);
        // 审核时间
        xUserInfo.setAuditTime(now);
        // 审核类型
        xUserInfo.setRefuseType(type);

        // region -- 审核通过
        if (flag == SysVariable.AUDIT_PASS) {
            // 更新用户状态
            xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_SUCCESS);

            // 获取风控信息
            String demo = xLoanService.queryRiskQualificationDemo(userGid, SysVariable.RISK_TYPE_AUDIT);
            if (!StringUtils.isEmpty(demo)) {
                // 风控自动拒绝
                xUserInfo.setRefuseType(SysVariable.AUDIT_REFUSE_TYPE_AUTO);
                // 拒绝理由
                xUserInfo.setRefuseReason(demo);
                // 用户状态
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_FAIL);
            }
        }
        // endregion

        // region -- 审核拒绝
        else {
            if (StringUtils.isEmpty(refuseCode)) {
                throw new BusinessException("请选择拒绝理由");
            }
            if (refuseCode.charAt(0) == '1') {
                // 1开头的 驳回重新提交
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_RETRY);
            } else if (refuseCode.charAt(0) == '2' || refuseCode.charAt(0) == '3') {
                // 2 和3 开头的 直接审核拒绝
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_FAIL);
            } else {
                // 黑名单拒绝
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_FAIL);
            }
            // 拒绝理由
            xUserInfo.setRefuseReason(refuseDemo);
        }
        // endregion

        // endregion

        // region -- 更新审核结果
        if (xUserInfoDao.update(xUserInfo) == 1) {
            // 更新授信结果上报事件
            if (!SysVariable.USER_STATUS_AUTH_RETRY.equals(xUserInfo.getUserStatus())) {
                // 记录待上报的app事件
                threadPoolService.execute(() -> {
                    XAppEvent xAppEvent = new XAppEvent();
                    xAppEvent.setType(SysVariable.APP_EVENT_AUDIT);
                    xAppEvent.setGid(userGid);
                    xAppEvent.setIsSuccess(xUserInfo.getUserStatus().equals(SysVariable.USER_STATUS_AUTH_SUCCESS) ? SysVariable.APP_EVENT_SUCCESS : SysVariable.APP_EVENT_FAIL);
                    restTemplate.postForObject(xAppEvents_event_url, xAppEvent, Response.class);
                });
            }

            if (SysVariable.USER_STATUS_AUTH_SUCCESS.equals(xUserInfo.getUserStatus())) {
                logger.info("{}：审核通过，开始初始化借款利率和借款额度", xUserInfo.getUserGid());
                xLoanService.initLoanRateAndCreditLimit(userGid);
                return 1;
            } else {
                logger.info("审核拒绝——被审核人: {}, 拒绝理由: {}.", userGid, refuseDemo);
                if (SysVariable.AUDIT_PASS == flag && SysVariable.AUDIT_REFUSE_TYPE_MANUAL.equals(type)) {
                    // 人工审核通过，但授信失败需要抛出异常给前端
                    throw new BusinessException("很抱歉，该用户的授信信息未通过审核");
                }
                return 0;
            }
        } else {
            logger.info("用户状态更新失败: {}", xUserInfo.getUserGid());
            return -1;
        }
        // endregion
    }

    @Autowired
    private BlackUserService blackUserService;

    /**
     * 对待授信用户进行自动审核或分配审计员
     *
     * @param userGid
     * @return
     */
    @Override
    public boolean autoAudit(String userGid) {
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        String phone;
        if (xUserInfo == null) {
            logger.warn("当前用户不存在:{}", userGid);
            return false;
        } else {
            phone = xUserInfo.getPhone();
            if (StringUtils.isEmpty(phone)) {
                logger.warn("找不到当前用户的电话号码:{}", userGid);
                return false;
            }
        }

        // region -- 用户风控配置
        Map userManageConfig = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_USER_MANAGE));
        Map config = (LinkedHashMap) userManageConfig.get("blackUser");

        // 判断黑名单用户是否允许黑名单通过授信
        boolean allowBlackFlag = (Boolean) config.get("audit");
        if (!allowBlackFlag) {
            // 黑名单用户 拒绝
            if (blackUserService.isBlackUser(xUserInfo.getPhone(), xUserInfo.getIdentityCard())) {
                logger.info("黑名单用户: {}", userGid);
                xUserInfoService.auditUpdate(userGid, SysVariable.AUDIT_REFUSE, SysVariable.AUDIT_BLACK_USER_CODE,
                        "黑名单用户: " + userGid, SysVariable.AUDIT_REFUSE_TYPE_AUTO);
                return false;
            }
        }

        // 白名单自动审核
        config = (LinkedHashMap) userManageConfig.get("whiteUser");
        boolean autoAuditFlag = (Boolean) config.get("autoAudit");
        logger.info("白名单启用自动审核：{}", autoAuditFlag);
        // endregion

        // region -- 白名单用户自动授信
        List<RiskUser> whiteList = riskUserService.selectUserListByPhone(phone);
        if (autoAuditFlag && whiteList.size() > 0) {
            logger.info("{}: 白名单用户，执行自动审核---start", phone);
            if (xUserInfoService.auditUpdate(userGid, SysVariable.AUDIT_PASS, null, null, SysVariable.AUDIT_REFUSE_TYPE_AUTO) == 1) {
                logger.info("{}: 审核通过...", phone);
            } else {
                logger.info("{}: 审核不通过...", phone);
            }
        }
        // endregion

        // region -- 非白名单用户分配审计员
        else {
            logger.info("{}: 非白名单用户，分配审计员...start", phone);
            if (xAuditService.assignAuditForUser(null, userGid) != null) {
                logger.info("{}: 分配审计员成功...end", phone);
            } else {
                logger.info("{}: 分配审计员失败...end", phone);
            }
        }
        // endregion
        return true;
    }

    /**
     * 获取与某用户同名得所有用户
     *
     * @param limit
     * @param num
     * @param userGid
     * @return
     */
    @Override
    public PageTableResponse listSameName(int limit, int num, String userGid) {
        PageTableRequest request = new PageTableRequest();
        request.getParams().put("userGid", userGid);
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);
        return new PageTableHandler(
                a -> xUserInfoDao.countSameName(a.getParams()),
                a -> xUserInfoDao.listSameName(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    /**
     * 查看逾期用户
     *
     * @param limit
     * @param num
     * @param type
     * @return
     */
    @Override
    public PageTableResponse overDueUsers(int limit, int num, String type) {
        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);
        // type 0 逾期天数为(0,3], 1 逾期天数为 3天以上
        request.getParams().put("type", type);
        return new PageTableHandler(
                a -> xUserInfoDao.countOverDueUsers(a.getParams()),
                a -> xUserInfoDao.overDueUsers(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Autowired
    private XRecordLoanDao xRecordLoanDao;

    /**
     * 查看逾期借款信息
     *
     * @param limit
     * @param num
     * @param userGid
     * @param type
     * @return
     */
    @Override
    public PageTableResponse overDueLoan(int limit, int num, String userGid, String type) {
        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);
        // type 0 逾期天数为(0,3], 1 逾期天数为 3天以上
        request.getParams().put("type", type);
        request.getParams().put("userGid", userGid);
        return new PageTableHandler(
                a -> xRecordLoanDao.countOverDueLoan(a.getParams()),
                a -> xRecordLoanDao.overDueLoan(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }
}
