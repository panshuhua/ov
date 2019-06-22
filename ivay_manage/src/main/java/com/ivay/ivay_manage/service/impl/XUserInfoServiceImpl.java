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
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dao.master.XUserRiskDao;
import com.ivay.ivay_repository.dto.XAuditDetail;
import com.ivay.ivay_repository.dto.XLoanQualification;
import com.ivay.ivay_repository.model.RiskUser;
import com.ivay.ivay_repository.model.XAppEvent;
import com.ivay.ivay_repository.model.XUserInfo;
import com.ivay.ivay_repository.model.XUserRisk;
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
    private XLoanRateService xLoanRateService;

    @Autowired
    private XConfigService xConfigService;

    @Autowired
    private XUserInfoService xUserInfoService;

    @Autowired
    private RiskUserService riskUserService;

    @Autowired
    private XAuditUserService xAuditUserService;

    @Resource
    private XUserRiskDao xUserRiskDao;

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
            String demo = queryRiskQualificationDemo(userGid, SysVariable.RISK_TYPE_AUDIT);
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
                xLoanRateService.initLoanRateAndCreditLimit(userGid);
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

    @Override
    public XLoanQualification getAuditQualificationObj(String userGid, int flag) {
        XLoanQualification xLoanQualification = xUserInfoDao.getAuditQualificationObj(userGid);
        //获取经纬度
        XUserRisk xUserRisk = xUserRiskDao.getGps(userGid);
        if (xLoanQualification == null) {
            throw new BusinessException("很抱歉，您的借款申请未通过审核");
        }
        xLoanQualification.setContactsNum(xUserInfoDao.countContacts(userGid));
        xLoanQualification.setOneMajorPhoneNum(xUserInfoDao.countMajorPhone(xLoanQualification.getMajorPhone()));
        xLoanQualification.setOnePhoneNum(xUserInfoDao.countOnePhone(xLoanQualification.getMacCode()));

        // 根据经纬度计算10m之内的gps数量
        if (xUserRisk != null) {
            if (xUserRisk.getLongitude() != null && xUserRisk.getLatitude() != null) {
                Integer gpsNum = xUserInfoDao.getUserCountsBygps(xUserRisk.getLongitude(), xUserRisk.getLatitude());
                if (gpsNum == null) {
                    gpsNum = 0;
                }
                xLoanQualification.setOneGpsNum(gpsNum);
            } else {
                xLoanQualification.setOneGpsNum(-1);
            }
        } else {
            xLoanQualification.setOneGpsNum(-1);
        }


        //实时计算社交类app个数
        String updateDate = DateUtils.dateToString_YYYY_MM_DD(new Date());
        Integer appNumCount = xUserRiskDao.queryAppNum(userGid, updateDate);
        xLoanQualification.setAppCount(appNumCount);

        return xLoanQualification;
    }

    @Override
    public XLoanQualification getLoanQualificationObj(XLoanQualification xLoanQualification, String userGid) {
        //查询历史最大逾期天数
        Integer maxOverdueDay = xUserInfoDao.getMaxOverdueDay(userGid);
        if (maxOverdueDay == null) {
            maxOverdueDay = 0;
        }
        xLoanQualification.setMaxOverdueDay(maxOverdueDay);

        //查询当前是否处于逾期中
        Integer overdueDaysnow = xUserInfoDao.getOverdueCountsNow(userGid);
        if (overdueDaysnow > 0) {
            xLoanQualification.setOverdueFlag(true);
        }
        //最近一笔结清交易逾期天数
        Integer lastOverdueDay = xUserInfoDao.getlastOverdueDay(userGid);
        if (lastOverdueDay == null) {
            lastOverdueDay = 0;
        }
        xLoanQualification.setLastOverdueDay(lastOverdueDay);

        // 实时计算14天内社交类app的最大个数
        String updateDate = DateUtils.dateToString_MM_DD(new Date());
        Integer appMaxCount = xUserRiskDao.queryMaxAppNum(userGid, updateDate);
        xLoanQualification.setAppMaxCount(appMaxCount);

        return xLoanQualification;
    }

    @Autowired
    private BlackUserService blackUserService;

    /**
     * 获得某人的风控审核结果，空字符串表示通过审核
     *
     * @param userGid
     * @param flag    0 授信策略 1 借款策略
     * @return 返回未通过审核的理由
     */
    @Override
    public String queryRiskQualificationDemo(String userGid, int flag) {
        // region --黑名单用户 拒绝
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        if (blackUserService.isBlackUser(xUserInfo.getPhone(), xUserInfo.getIdentityCard())) {
            logger.info("黑名单用户: {}", userGid);
            return "黑名单用户: " + userGid;
        }
        // endregion

        // region --风控管理配置
        Map riskConfig = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_CREDIT_RISK));
        if (riskConfig == null || "false".equals(riskConfig.get("enable").toString())) {
            return null;
        }

        // 获取风控对象
        XLoanQualification xLoanQualification = getAuditQualificationObj(userGid, flag);
        // 借款需要查询逾期记录
        if (flag == SysVariable.RISK_TYPE_LOAN) {
            xLoanQualification = getLoanQualificationObj(xLoanQualification, userGid);
        }
        // endregion

        Map config = (LinkedHashMap) riskConfig.get(flag == SysVariable.RISK_TYPE_AUDIT ? "audit" : "loan");
        StringBuilder sb = new StringBuilder();
        for (Object key : config.keySet()) {
            String[] values = config.get(key).toString().split("~");
            int min = Integer.parseInt(values[0]);
            int max = (values.length == 1) ? Integer.MAX_VALUE : Integer.parseInt(values[1]);
            // region --风控风控规则返回拒绝信息
            switch (key.toString()) {
                case "age":
                    // 年龄<18或者年龄>50岁时，则拒贷
                    if (xLoanQualification.getAge() < min || xLoanQualification.getAge() > max) {
                        sb.append("年龄不符: ").append(xLoanQualification.getAge()).append(";");
                    }
                    break;
                case "contact":
                    // 实时计算的联系人个数<10且14天内的最大联系人个数<10,拒贷（人数使用配置）
                    if (xLoanQualification.getContactsNum() < min || xLoanQualification.getContactsNum() > max) {
                        sb.append("联系人个数不符: ").append(xLoanQualification.getContactsNum()).append(";");
                    }
                    break;
                case "gps":
                    // 近一个月同一gps(精确到10m)注册用户数>1，拒贷
                    if (xLoanQualification.getOneGpsNum() < min || xLoanQualification.getOneGpsNum() > max) {
                        sb.append("近一个月同一gps(精确到10m)注册用户数不符: ").append(xLoanQualification.getOneGpsNum()).append(";");
                    }
                    break;
                case "macCode":
                    // 近一个月同一设备id注册用户数>1，拒贷
                    if (xLoanQualification.getOnePhoneNum() < min || xLoanQualification.getOnePhoneNum() > max) {
                        sb.append("近一个月同一设备id注册用户数不符: ").append(xLoanQualification.getOnePhoneNum()).append(";");
                    }
                    break;
                case "majorRelation":
                    // 亲密联系人号码出现次数> 2，拒贷（人数使用配置）
                    if (xLoanQualification.getOneMajorPhoneNum() < min || xLoanQualification.getOneMajorPhoneNum() > max) {
                        sb.append("亲密联系人号码出现次数不符: ").append(xLoanQualification.getOneMajorPhoneNum()).append(";");
                    }
                    break;
                case "appNum":
                    //社交类app的个数不符
                    //贷前策略
                    if (flag == 0) {
                        if (xLoanQualification.getAppCount() <= min) {
                            sb.append("社交类app个数不符: ").append(xLoanQualification.getAppCount()).append(";");
                        }
                    }

                    //贷中策略
                    if (flag == 1) {
                        if (xLoanQualification.getAppCount() <= min && xLoanQualification.getAppMaxCount() <= min) {
                            sb.append("社交类app个数不符: ").append(xLoanQualification.getAppCount()).append(";");
                        }
                    }

                    break;
                case "overdueDay":
                    //历史最大逾期天数>=30天，拒贷
                    if (xLoanQualification.getMaxOverdueDay() >= max) {
                        sb.append("历史最大逾期天数不符: ").append(xLoanQualification.getMaxOverdueDay()).append(";");
                    }
                    break;
                case "overdueDay2":
                    //历史最大逾期天数在[15,30)天且最近一笔结清交易逾期天数大于5天，拒贷
                    if (xLoanQualification.getMaxOverdueDay() >= 15
                            && xLoanQualification.getMaxOverdueDay() < 30
                            && xLoanQualification.getLastOverdueDay() > max) {
                        sb.append("历史最大逾期天数和结清交易逾期天数不符: ").append(xLoanQualification.getMaxOverdueDay()).append(";");
                    }
                    break;
                default:
                    logger.info("未知的类型：{}", key.toString());
            }
            // endregion
        }
        //当前处于逾期中，拒贷
        if (xLoanQualification.isOverdueFlag()) {
            sb.append("当前处于逾期中: ").append(userGid).append(";");
        }
        return sb.toString();
    }

    /**
     * 对待授信用户进行自动审核或分配审计员
     *
     * @param userGid
     * @return
     */
    @Override
    public boolean autoAudit(String userGid) {
        String phone = xUserInfoDao.getPhone(userGid);
        if (StringUtils.isEmpty(phone)) {
            throw new BusinessException("找不到当前用户的电话号码");
        }

        // region -- 黑名单用户 拒绝
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        if (blackUserService.isBlackUser(xUserInfo.getPhone(), xUserInfo.getIdentityCard())) {
            logger.info("黑名单用户: {}", userGid);
            xUserInfoService.auditUpdate(userGid, SysVariable.AUDIT_REFUSE, SysVariable.AUDIT_BLACK_USER_CODE,
                    "黑名单用户: " + userGid, SysVariable.AUDIT_REFUSE_TYPE_AUTO);
            return false;
        }
        // endregion

        // 滞纳金配置
        boolean autoAuditFlag = false;
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_AUTO_AUDIT));
        if (config != null) {
            autoAuditFlag = (Boolean) config.get("whiteUser");
            logger.info("白名单启用自动审核：{}", autoAuditFlag);
        }

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
            if (xAuditUserService.assignAuditForUser(null, userGid) != null) {
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
     * @param request
     * @return
     */
    @Override
    public PageTableResponse listSameName(PageTableRequest request) {
        return new PageTableHandler(
                a -> xUserInfoDao.countSameName(a.getParams()),
                a -> xUserInfoDao.listSameName(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }
}
