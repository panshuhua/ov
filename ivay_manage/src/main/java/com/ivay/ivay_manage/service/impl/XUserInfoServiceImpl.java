package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.config.I18nService;
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
import com.ivay.ivay_repository.model.RiskUser;
import com.ivay.ivay_repository.model.XAuditDetail;
import com.ivay.ivay_repository.model.XLoanQualification;
import com.ivay.ivay_repository.model.XUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * 提交审核结果
     *
     * @param userGid
     * @param flag
     * @param refuseCode
     * @param refuseDemo
     * @param type       审核类型 0人工 1自动
     * @return
     */
    @Override
    public int auditUpdate(String userGid, int flag, String refuseCode, String refuseDemo, String type) {
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        if (xUserInfo == null) {
            throw new BusinessException(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
        }

        // 审核通过
        if (flag == SysVariable.AUDIT_PASS) {
            String demo = queryRiskQualificationDemo(userGid, SysVariable.RISK_TYPE_AUDIT);
            if (!StringUtils.isEmpty(demo)) {
                xUserInfo.setRefuseType(SysVariable.AUDIT_REFUSE_TYPE_AUTO);
                xUserInfo.setAuditTime(new Date());
                xUserInfo.setRefuseReason(demo);
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_FAIL);
                xUserInfoDao.update(xUserInfo);
                throw new BusinessException("很抱歉，该用户的授信信息未通过审核");
            }
            xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_SUCCESS);
        } else {
            // 审核拒绝
            if (StringUtils.isEmpty(refuseCode)) {
                throw new BusinessException("请选择拒绝理由");
            }
            if (refuseCode.charAt(0) == '1') {
                // 1开头的 驳回重新提交
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_RETRY);
            } else {
                // 2 和3 开头的 直接审核拒绝
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_FAIL);
            }
            // 拒绝理由
            xUserInfo.setRefuseReason(refuseDemo);
            logger.info("审核拒绝——被审核人: {}, 拒绝理由: {}:{}.", userGid, refuseCode, refuseDemo);
        }
        Date now = new Date();
        xUserInfo.setUpdateTime(now);
        // 审核时间
        xUserInfo.setAuditTime(now);
        // 审核类型
        xUserInfo.setRefuseType(type);
        if (xUserInfoDao.update(xUserInfo) == 1 && SysVariable.USER_STATUS_AUTH_SUCCESS.equals(xUserInfo.getUserStatus())) {
            logger.info("审核通过，开始初始化借款利率和借款额度");
            xLoanRateService.initLoanRateAndCreditLimit(userGid);
        } else {
            logger.info("审核拒绝");
        }
        return flag;
    }


    @Override
    public XLoanQualification getAuditQualificationObj(String userGid, int flag) {
        XLoanQualification xLoanQualification = xUserInfoDao.getAuditQualificationObj(userGid);
        if (xLoanQualification == null) {
            throw new BusinessException("很抱歉，您的借款申请未通过审核");
        }
        xLoanQualification.setContactsNum(xUserInfoDao.countContacts(userGid));
        xLoanQualification.setOneMajorPhoneNum(xUserInfoDao.countMajorPhone(xLoanQualification.getMajorPhone()));
        xLoanQualification.setOnePhoneNum(xUserInfoDao.countOnePhone(xLoanQualification.getMacCode()));
        // 根据经纬度计算10m之内的gps数量
        if (xLoanQualification.getLongitude() != null && xLoanQualification.getLatitude() != null) {
            Integer gpsNum = xUserInfoDao.getUserCountsBygps(xLoanQualification.getLongitude(), xLoanQualification.getLatitude());
            if (gpsNum == null) {
                gpsNum = 0;
            }
            xLoanQualification.setOneGpsNum(gpsNum);
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

    /**
     * 获得某人的风控审核结果，空字符串表示通过审核
     *
     * @param userGid
     * @param flag    0 贷前策略 1 贷中策略
     * @return 返回未通过审核的理由
     */
    @Override
    public String queryRiskQualificationDemo(String userGid, int flag) {
        // 風控管理配置
        Map riskConfig = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_CREDIT_RISK));
        if (riskConfig == null || "false".equals(riskConfig.get("enable").toString())) {
            return null;
        }

        XLoanQualification xLoanQualification = getAuditQualificationObj(userGid, flag);
        if (flag == SysVariable.RISK_TYPE_LOAN) {
            xLoanQualification = getLoanQualificationObj(xLoanQualification, userGid);
        }

        Map config = (LinkedHashMap) riskConfig.get(flag == SysVariable.RISK_TYPE_AUDIT ? "audit" : "loan");
        for (Object key : config.keySet()) {
            String[] values = config.get(key).toString().split("~");
            int min = Integer.parseInt(values[0]);
            int max = (values.length == 1) ? Integer.MAX_VALUE : Integer.parseInt(values[1]);
            switch (key.toString()) {
                case "age":
                    // 年龄<18或者年龄>50岁时，则拒贷
                    if (xLoanQualification.getAge() < min || xLoanQualification.getAge() > max) {
                        logger.info("年龄不符: {}", xLoanQualification.getAge());
                        return "年龄不符: " + xLoanQualification.getAge();
                    }
                    break;
                case "contact":
                    // 实时计算的联系人个数<10且14天内的最大联系人个数<10,拒贷（人数使用配置）
                    if (xLoanQualification.getContactsNum() < min || xLoanQualification.getContactsNum() > max) {
                        logger.info("联系人个数不符: {}", xLoanQualification.getContactsNum());
                        return "联系人个数不符: " + xLoanQualification.getContactsNum();
                    }
                    break;
                case "gps":
                    // 近一个月同一gps(精确到10m)注册用户数>1，拒贷
                    if (xLoanQualification.getOneGpsNum() < min || xLoanQualification.getOneGpsNum() > max) {
                        logger.info("近一个月同一gps(精确到10m)注册用户数不符: {}", xLoanQualification.getOneGpsNum());
                        return "近一个月同一gps(精确到10m)注册用户数不符: " + xLoanQualification.getOneGpsNum();
                    }
                    break;
                case "macCode":
                    // 近一个月同一设备id注册用户数>1，拒贷
                    if (xLoanQualification.getOnePhoneNum() < min || xLoanQualification.getOnePhoneNum() > max) {
                        logger.info("近一个月同一设备id注册用户数不符: {}", xLoanQualification.getOnePhoneNum());
                        return "近一个月同一设备id注册用户数不符: " + xLoanQualification.getOnePhoneNum();
                    }
                    break;
                case "majorRelation":
                    // 亲密联系人号码出现次数> 2，拒贷（人数使用配置）
                    if (xLoanQualification.getOneMajorPhoneNum() < min || xLoanQualification.getOneMajorPhoneNum() > max) {
                        logger.info("亲密联系人号码出现次数不符: {}", xLoanQualification.getOneMajorPhoneNum());
                        return "亲密联系人号码出现次数不符: " + xLoanQualification.getOneMajorPhoneNum();
                    }
                    break;
                case "appNum":
                    //社交类app的个数不符
                    if (xLoanQualification.getAppCount() <= min || xLoanQualification.getAppMaxCount() <= min) {
                        logger.info("社交类app个数不符: {}", xLoanQualification.getAppCount());
                        return "社交类app个数不符: " + xLoanQualification.getAppCount();
                    }
                case "overdueDay":
                    //历史最大逾期天数>=30天，拒贷
                    if (xLoanQualification.getMaxOverdueDay() >= max) {
                        logger.info("历史最大逾期天数不符: {}", xLoanQualification.getMaxOverdueDay());
                        return "历史最大逾期天数不符: " + xLoanQualification.getMaxOverdueDay();
                    }
                case "overdueDay2":
                    //历史最大逾期天数在[15,30)天且最近一笔结清交易逾期天数大于5天，拒贷
                    if (xLoanQualification.getMaxOverdueDay() >= 15 && xLoanQualification.getMaxOverdueDay() < 30 && xLoanQualification.getLastOverdueDay() > max) {
                        logger.info("历史最大逾期天数和结清交易逾期天数不符: {}", xLoanQualification.getMaxOverdueDay());
                        return "历史最大逾期天数和结清交易逾期天数不符: " + xLoanQualification.getMaxOverdueDay();
                    }
                default:
                    logger.info("未知的类型：{}", key.toString());
            }
        }
        //当前处于逾期中，拒贷
        if (xLoanQualification.isOverdueFlag()) {
            logger.info("当前处于逾期中");
            return "有逾期借款未还";
        }
        return null;
    }

    /**
     * 对提交提交用户进行自动审核处理
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

        // region -- 白名单用户自动授权
        List<RiskUser> whiteList = riskUserService.selectUserListByPhone(phone);
        if (whiteList.size() > 0) {
            logger.info("{}: 白名单用户，执行自动审核---start", phone);
            if (xUserInfoService.auditUpdate(userGid, 1, null, null, SysVariable.AUDIT_REFUSE_TYPE_AUTO) == 1) {
                logger.info("{}: 审核成功...", phone);
            } else {
                logger.info("{}: 审核失败...", phone);
            }
        }
        // endregion

        // region -- 非白名单用户分配审计员
        else {
            logger.info("{}: 非白名单用户，分配审计员...", phone);
            if (xAuditUserService.assignAuditForUser(null, userGid) != null) {
                logger.info("{}: 分配审计员成功...", phone);
            } else {
                logger.info("{}: 分配审计员失败...", phone);
            }
        }
        // endregion
        return true;
    }
}
