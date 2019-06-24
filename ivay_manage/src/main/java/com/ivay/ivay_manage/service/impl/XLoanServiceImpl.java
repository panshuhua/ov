package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.service.*;
import com.ivay.ivay_repository.dao.master.XLoanRateDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dao.master.XUserRiskDao;
import com.ivay.ivay_repository.dto.XLoanQualification;
import com.ivay.ivay_repository.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class XLoanServiceImpl implements XLoanService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Resource
    private XLoanRateDao xLoanRateDao;

    @Resource
    private XConfigService xConfigService;

    @Resource
    private XUserInfoDao xUserInfoDao;

    @Resource
    private XRecordLoanDao xRecordLoanDao;

    @Autowired
    private ThreadPoolService threadPoolService;

    @Autowired
    private RiskUserService riskUserService;

    @Resource
    private XUserRiskDao xUserRiskDao;

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
        // region --用户风控管理
        Map userManageConfig = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_USER_MANAGE));
        if (userManageConfig != null) {
            Map config = (LinkedHashMap) userManageConfig.get("blackUser");
            boolean allowBlackFlag = (Boolean) config.get(flag == SysVariable.RISK_TYPE_AUDIT ? "audit" : "loan");
            logger.info("{}允许黑名单通过风控", allowBlackFlag ? "" : "不");

            XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
            if (!allowBlackFlag) {
                // 黑名单用户 拒绝
                if (blackUserService.isBlackUser(xUserInfo.getPhone(), xUserInfo.getIdentityCard())) {
                    logger.info("黑名单用户: {}", userGid);
                    return "黑名单用户: " + userGid;
                }
            }

            config = (LinkedHashMap) userManageConfig.get("normalUser");
            allowBlackFlag = (Boolean) config.get(flag == SysVariable.RISK_TYPE_AUDIT ? "audit" : "loan");
            logger.info("{}允许自然人通过风控", allowBlackFlag ? "" : "不");

            if (!allowBlackFlag) {
                // 自然人（非白名单用户和黑名单用户）
                List<RiskUser> whiteList = riskUserService.selectUserListByPhone(xUserInfo.getPhone());
                if (whiteList.size() == 0) {
                    logger.info("非白名单用户，不允许通过风控: {}", userGid);
                    return "非白名单用户，不允许通过风控: " + userGid;
                }
            }
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

    @Override
    public void initLoanRateAndCreditLimit(String userGid) {
        // 授信額度
        threadPoolService.execute(() -> {
            logger.info("开始计算授信额度");
            refreshCreditLimit(userGid);
        });
        // 借款利率
        threadPoolService.execute(() -> {
            logger.info("开始计算借款利率");
            saveLoanRate(userGid);
        });
    }

    @Override
    public int saveLoanRate(String userGid) {
        List<XLoanRate> list = xLoanRateDao.getByGid(userGid);
        if (list.size() > 0) {
            logger.info("已有借款利率，不需要重新配置");
            return list.size();
        }

        // 借款利率配置
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_LOAN_RATE));
        Date now = new Date();
        for (Object key : config.keySet()) {
            BigDecimal value = new BigDecimal(config.get(key).toString());
            XLoanRate xlr = new XLoanRate();
            xlr.setCreateTime(now);
            xlr.setUpdateTime(now);
            xlr.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
            xlr.setUserGid(userGid);
            xlr.setPeriod(Integer.parseInt(key.toString()));
            xlr.setInterestRate(value);
            list.add(xlr);
        }
        return xLoanRateDao.saveByBatch(list);
    }

    @Override
    public long refreshCreditLimit(String userGid) {
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        if (xUserInfo == null) {
            throw new BusinessException("0014", "用户不存在");
        }
        if (checkCreditLimitFlag(xUserInfo)) {
            logger.info("进行提额..");
            updateCreditLimit(xUserInfo);
        }
        return xUserInfo.getCreditLine();
    }

    /**
     * 判断是否进行提额或初始化授信额度
     *
     * @param xUserInfo
     * @return
     */
    private boolean checkCreditLimitFlag(XUserInfo xUserInfo) {
        if (xUserInfo.getCreditLine() == null || xUserInfo.getCreditLine() == 0) {
            // 未初始化授信额度，返回true
            return true;
        }

        // 判断提额权限
        Map<String, Object> params = new HashMap<>();
        params.put("userGid", xUserInfo.getUserGid());
        List<XRecordLoan> list = xRecordLoanDao.list(params, 1, 0);
        // 已初始化授信额度，但没借过钱
        if (list.size() == 0) {
            return false;
        }

        // 计算最后一笔结清订单的逾期时间
        long overdueDay = 0;
        Date lastRepaymentDay = null;
        for (XRecordLoan xrl : list) {
            // 已还清的借款
            if (SysVariable.REPAYMENT_STATUS_SUCCESS == xrl.getRepaymentStatus()) {
                // 最近一笔还款
                if (lastRepaymentDay == null || DateUtils.isDateAfter(lastRepaymentDay, xrl.getLastRepaymentTime()) < 0) {
                    lastRepaymentDay = xrl.getLastRepaymentTime();
                    // 逾期
                    if (DateUtils.isDateAfter(lastRepaymentDay, xrl.getDueTime()) > 0) {
                        overdueDay = Long.parseLong(DateUtils.getTwoDay(xrl.getDueTime(), lastRepaymentDay));
                    }
                }
            }
        }
        // 最近一笔结清订单的逾期天数>5不提额
        if (overdueDay > 5) {
            return false;
        }

        // 與第一次交易的天數間隔
        Date firstRepaymentTime = xRecordLoanDao.getFirstRepaymentTime(xUserInfo.getUserGid());
        if (firstRepaymentTime == null) {
            return false;
        }
        long diff = Long.parseLong(DateUtils.getTwoDay(firstRepaymentTime, new Date()));
        int count = xUserInfo.getCreditLineCount() == null ? 0 : xUserInfo.getCreditLineCount();
        // 根据实际还清借款时间触发, 第N次提额时间距离首笔交易时间分别不小于5*N天
        return 5 * (count + 1) <= diff;
    }

    private void updateCreditLimit(XUserInfo xUserInfo) {
        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_CREDIT_LIMIT));
        if (config == null) {
            logger.error("提額配置获取出错");
        } else {
            String typeFlag = "normal";
            long borrowAmount = 0;
            // 判斷 用户 是白名單用戶還是正常用戶
            List<RiskUser> whiteList = riskUserService.selectUserListByPhone(xUserInfo.getPhone());
            if (whiteList.size() > 0) {
                typeFlag = "white";
                borrowAmount = Long.parseLong(whiteList.get(0).getAmount());
            }
            Map creditConfig = (LinkedHashMap) config.get(typeFlag);

            if (xUserInfo.getCreditLine() == null || xUserInfo.getCreditLine() == 0) {
                // 设置授信额度
                if (borrowAmount == 0) {
                    borrowAmount = Long.parseLong(creditConfig.get("start").toString());
                }
                xUserInfo.setCreditLine(borrowAmount);
                xUserInfo.setCanborrowAmount(xUserInfo.getCreditLine());
                xUserInfo.setCreditLineCount(0);
                xUserInfoDao.update(xUserInfo);
            } else {
                // 提额
                Map<String, Object> params = new HashMap<>();
                params.put("userGid", xUserInfo.getUserGid());
                params.put("repaymentStatus", SysVariable.REPAYMENT_STATUS_SUCCESS);
                int count = xRecordLoanDao.count(params);
                if (count > 0) {
                    Object step = ((LinkedHashMap) creditConfig.get("step")).get(String.valueOf(count));
                    if (step == null) {
                        step = ((LinkedHashMap) creditConfig.get("step")).get(">");
                    }
                    long s = Long.parseLong(step.toString());
                    long max = Long.parseLong(creditConfig.get("end").toString());
                    if (max < s + xUserInfo.getCreditLine()) {
                        s = max - xUserInfo.getCreditLine();
                    }
                    xUserInfo.setCreditLine(s + xUserInfo.getCreditLine());
                    xUserInfo.setCanborrowAmount(s + xUserInfo.getCanborrowAmount());
                    int n = xUserInfo.getCreditLineCount() == null ? 0 : xUserInfo.getCreditLineCount();
                    xUserInfo.setCreditLineCount(n + 1);
                    logger.info("第{}次提額，提額數目: {}, 新額度: {}", xUserInfo.getCreditLineCount(), s, xUserInfo.getCreditLine());
                    xUserInfoDao.update(xUserInfo);
                }
            }
        }
    }

}
