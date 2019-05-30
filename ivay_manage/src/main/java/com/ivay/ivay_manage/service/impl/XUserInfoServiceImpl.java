package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.advice.BusinessException;
import com.ivay.ivay_manage.service.XConfigService;
import com.ivay.ivay_manage.service.XLoanRateService;
import com.ivay.ivay_manage.service.XUserInfoService;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.XAuditDetail;
import com.ivay.ivay_repository.model.XLoanQualification;
import com.ivay.ivay_repository.model.XUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class XUserInfoServiceImpl implements XUserInfoService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Resource
    private XUserInfoDao xUserInfoDao;
    @Resource
    private XLoanRateService xLoanRateService;

    @Autowired
    private XConfigService xConfigService;

    @Override
    public PageTableResponse auditList(PageTableRequest request) {
        return new PageTableHandler((a) -> xUserInfoDao.auditCount(a.getParams()),
                (a) -> xUserInfoDao.auditList(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public XAuditDetail auditDetail(String userGid) {
        return xUserInfoDao.auditDetail(userGid);
    }

    @Override
    public int auditUpdate(String userGid, int flag) {
        XUserInfo xUserInfo = xUserInfoDao.getByGid(userGid);
        if (xUserInfo == null) {
            throw new BusinessException("0014", "该用户不存在, 或已冻结");
        }
        switch (flag) {
            case 0:
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_FAIL);
                break;
            case 1:
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_SUCCESS);
                if (!queryAuditQualification(userGid, 0)) {
                    throw new BusinessException("很抱歉，该用户的授信信息未通过审核");
                }
                break;
            case 2:
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_AUTH_RETRY);
                break;
        }
        xUserInfo.setUpdateTime(new Date());
        if (xUserInfoDao.update(xUserInfo) == 1 && SysVariable.USER_STATUS_AUTH_SUCCESS.equals(xUserInfo.getUserStatus())) {
            logger.info("审核通过，开始初始化借款利率和借款额度");
            xLoanRateService.initLoanRateAndCreditLimit(userGid);
        } else {
            logger.info("审核不通过");
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
        Integer gpsNum = xUserInfoDao.getUserCountsBygps(xLoanQualification.getLongitude(), xLoanQualification.getLatitude());
        if (gpsNum == null) {
            gpsNum = 0;
        }
        xLoanQualification.setOneGpsNum(gpsNum);
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
        return xLoanQualification;
    }

    @Override
    public boolean queryAuditQualification(String userGid, int flag) {
        // todo new 風控管理配置
        Map riskConfig = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.TEMPLATE_CREDIT_RISK));
        if (riskConfig == null || "false".equals(riskConfig.get("enable").toString())) {
            return true;
        }

        XLoanQualification xLoanQualification = getAuditQualificationObj(userGid, flag);
        if (flag == 1) {
            xLoanQualification = getLoanQualificationObj(xLoanQualification, userGid);
        }

        Map config = (LinkedHashMap) riskConfig.get(flag == 0 ? "audit" : "loan");
        for (Object key : config.keySet()) {
            String[] values = config.get(key).toString().split("~");
            int min = Integer.parseInt(values[0]);
            int max = (values.length == 1) ? Integer.MAX_VALUE : Integer.parseInt(values[1]);
            switch (key.toString()) {
                case "age":
                    // 年龄<18或者年龄>50岁时，则拒贷
                    if (xLoanQualification.getAge() < min || xLoanQualification.getAge() > max) {
                        logger.info("年龄不符");
                        return false;
                    }
                    break;
                case "contact":
                    // 实时计算的联系人个数<10且14天内的最大联系人个数<10,拒贷（人数使用配置）
                    if (xLoanQualification.getContactsNum() < min || xLoanQualification.getContactsNum() > max) {
                        logger.info("联系人个数不符");
                        return false;
                    }
                    break;
                case "gps":
                    // 近一个月同一gps(精确到10m)注册用户数>1，拒贷
                    if (xLoanQualification.getOneGpsNum() < min || xLoanQualification.getOneGpsNum() > max) {
                        logger.info("近一个月同一gps(精确到10m)注册用户数不符");
                        return false;
                    }
                    break;
                case "macCode":
                    // 近一个月同一设备id注册用户数>1，拒贷
                    if (xLoanQualification.getOnePhoneNum() < min || xLoanQualification.getOnePhoneNum() > max) {
                        logger.info("近一个月同一设备id注册用户数不符");
                        return false;
                    }
                    break;
                case "majorRelation":
                    // 亲密联系人号码出现次数> 2，拒贷（人数使用配置）
                    if (xLoanQualification.getOneMajorPhoneNum() < min || xLoanQualification.getOneMajorPhoneNum() > max) {
                        logger.info("亲密联系人号码出现次数不符");
                        return false;
                    }
                    break;
                case "overdueDay":
                    //历史最大逾期天数>=30天，拒贷
                    if (xLoanQualification.getMaxOverdueDay() >= max) {
                        logger.info("历史最大逾期天数不符");
                        return false;
                    }
                case "overdueDay2":
                    //历史最大逾期天数在[15,30)天且最近一笔结清交易逾期天数大于5天，拒贷
                    if (xLoanQualification.getMaxOverdueDay() >= 15 && xLoanQualification.getMaxOverdueDay() < 30 && xLoanQualification.getLastOverdueDay() > max) {
                        logger.info("历史最大逾期天数和结清交易逾期天数不符");
                        return false;
                    }
            }
        }
        //当前处于逾期中，拒贷
        if (xLoanQualification.isOverdueFlag()) {
            logger.info("当前处于逾期中");
            return false;
        }
        return true;
    }

}
