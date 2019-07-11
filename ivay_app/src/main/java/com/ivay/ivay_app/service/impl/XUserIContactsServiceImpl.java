package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.service.XUserContactsService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XUserAppNumDao;
import com.ivay.ivay_repository.dao.master.XUserContactsDao;
import com.ivay.ivay_repository.dao.master.XUserRiskDao;
import com.ivay.ivay_repository.dto.XRiskInfo;
import com.ivay.ivay_repository.model.XUserAppNum;
import com.ivay.ivay_repository.model.XUserContacts;
import com.ivay.ivay_repository.model.XUserInfo;
import com.ivay.ivay_repository.model.XUserRisk;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class XUserIContactsServiceImpl implements XUserContactsService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private XUserContactsDao xUserContactsDao;

    @Autowired
    private XUserAppNumDao xUserAppNumDao;

    @Autowired
    private XUserRiskDao xUserRiskDao;

    @Override
    public boolean saveAll(String type, XRiskInfo riskInfo, HttpServletRequest request) throws Exception {
        String gid = riskInfo.getUserGid();
        XUserInfo xUserInfo = new XUserInfo();
        xUserInfo.setUserGid(gid);
        xUserInfo.setUpdateTime(new Date());
        String updateDate = DateUtils.getNowDateYYYY_MM_DD();
        XUserRisk xUserRisk = new XUserRisk();
        xUserRisk.setUserGid(gid);
        xUserRisk.setCreateTime(new Date());
        xUserRisk.setUpdateTime(new Date());

        BigDecimal longitude = riskInfo.getLongitude();
        BigDecimal latitude = riskInfo.getLatitude();
        String imei = riskInfo.getImei();
        String imsi = riskInfo.getImsi();
        String phoneNumber = riskInfo.getPhoneNumber();
        String carrierName = riskInfo.getCarrierName();
        String phoneModel = riskInfo.getPhoneModel();
        String uid = riskInfo.getUid();
        String wifiMacAddress = riskInfo.getWifiMacAddress();
        String systemVersion = riskInfo.getSystemVersion();
        String ipv4Address = getPublicNetworkIP(request);
        logger.info("后台获取到的IP地址：" + ipv4Address + "-----------------");

        xUserRisk.setLongitude(longitude);
        xUserRisk.setLatitude(latitude);
        xUserRisk.setImei(imei);
        xUserRisk.setPhoneNumber(phoneNumber);
        xUserRisk.setCarrierName(carrierName);
        xUserRisk.setPhoneModel(phoneModel);
        xUserRisk.setUid(uid);
        xUserRisk.setWifiMacAddress(wifiMacAddress);
        xUserRisk.setSystemVersion(systemVersion);
        xUserRisk.setIpv4Address(ipv4Address);
        xUserRisk.setImsi(imsi);

        if (type != null) {
            // 联系人：每天都要上传记录
            if (SysVariable.CONTACT.equals(type)) {
                saveContacts(riskInfo, gid, updateDate);
            }
            // 社交类app的个数-每天都要记录
            if (SysVariable.APPNUM.equals(type)) {
                saveAppNum(riskInfo, gid, updateDate);
            }

            // 其他风控数据
            if (SysVariable.GPS.equals(type)) {
                saveGps(gid, xUserRisk);
            }

            // mac地址等其他信息
            if (SysVariable.OTHER.equals(type)) {
                saveOtherRisk(xUserRisk, gid);
            }

            // 适配之前旧版本的app
        } else {
            saveContacts(riskInfo, gid, updateDate);
            saveAppNum(riskInfo, gid, updateDate);
            saveGps(gid, xUserRisk);
            saveOtherRisk(xUserRisk, gid);
        }

        return true;
    }

    private void saveContacts(XRiskInfo riskInfo, String gid, String updateDate) {
        Set<XUserContacts> contacts = riskInfo.getContacts();
        Set<XUserContacts> xUserContacts = new HashSet<>();
        // 查询当天该用户有没有上传通讯录，如果上传了就不再重复上传了
        int contactsCount = xUserContactsDao.findContactsByUserGid(gid, updateDate);
        if (contactsCount <= 0) {
            if (contacts != null) {
                if (contacts.size() > 0) {
                    contacts.forEach(u -> {
                        try {
                            XUserContacts xUserContact =
                                    new XUserContacts(gid, updateDate, u.getContactName(), u.getPhoneNumber());
                            xUserContacts.add(xUserContact);
                            if (xUserContacts.size() >= 200) {
                                xUserContactsDao.insertBatchContacts(xUserContacts);
                                xUserContacts.clear();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                    xUserContactsDao.insertBatchContacts(xUserContacts);
                }
            }
        }
    }

    private void saveAppNum(XRiskInfo riskInfo, String gid, String updateDate) {
        XUserAppNum xUserAppNum = new XUserAppNum();
        xUserAppNum.setAppNum(riskInfo.getAppNum());
        xUserAppNum.setUpdateDate(updateDate);
        xUserAppNum.setUserGid(gid);

        // 查询当天是否已上传appNum，如果没有上传才能保存数据
        Integer count = xUserAppNumDao.countAppNum(gid, updateDate);
        if (count <= 0) {
            xUserAppNumDao.saveAppNum(xUserAppNum);
        }

    }

    private void saveGps(String gid, XUserRisk xUserRisk) {
        // 查询风控数据表有无该gid的用户，如果没有就新增一条记录
        Integer userNum = xUserRiskDao.countByUserGid(gid);
        logger.info("gps风控表中已有的数据个数：" + userNum + "-------------------");

        if (userNum == 0 || userNum == null) {
            logger.info("保存风控表数据gps：" + userNum + "-------------------");
            xUserRiskDao.save(xUserRisk);
        } else {
            logger.info("更新风控表数据gps：" + userNum + "-------------------");
            xUserRiskDao.updateGpsInfo(xUserRisk);
        }

    }

    private void saveOtherRisk(XUserRisk xUserRisk, String gid) {
        // 查询风控数据表有无该gid的用户，如果没有就新增一条记录
        Integer userNum = xUserRiskDao.countByUserGid(gid);
        logger.info("other风控表中已有的数据个数：" + userNum + "-------------------");
        if (userNum == null || userNum == 0) {
            logger.info("保存风控表数据other：" + userNum + "-------------------");
            xUserRiskDao.save(xUserRisk);
        } else {
            logger.info("保存风控表数据other：" + userNum + "-------------------");
            xUserRiskDao.updateOthers(xUserRisk);
        }
    }

    /**
     * 根据请求获取公网IP地址
     *
     * @param request
     * @return
     * @throws Exception
     */
    private String getPublicNetworkIP(HttpServletRequest request) throws Exception {
        if (request == null) {
            throw (new Exception("getIpAddr method HttpServletRequest Object is null"));
        }
        String ipString = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip  
        final String[] arr = ipString.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ipString = str;
                break;
            }
        }

        return ipString;
    }

}
