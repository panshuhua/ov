package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.service.XUserContactsService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_repository.dao.master.XUserAppNumDao;
import com.ivay.ivay_repository.dao.master.XUserContactsDao;
import com.ivay.ivay_repository.dao.master.XUserRiskDao;
import com.ivay.ivay_repository.dto.XRiskInfo;
import com.ivay.ivay_repository.model.XUserAppNum;
import com.ivay.ivay_repository.model.XUserContacts;
import com.ivay.ivay_repository.model.XUserInfo;
import com.ivay.ivay_repository.model.XUserRisk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    //前台还未传type
    @Override
    public boolean saveAll(XRiskInfo riskInfo) {
    	String type = riskInfo.getType();
        String gid = riskInfo.getUserGid();
        XUserInfo xUserInfo = new XUserInfo();
        xUserInfo.setUserGid(gid);
        xUserInfo.setUpdateTime(new Date());
        String updateDate = DateUtils.getNowDateYYYY_MM_DD();
        XUserRisk xUserRisk=new XUserRisk();
        xUserRisk.setUserGid(gid);
        xUserRisk.setCreateTime(new Date());
        xUserRisk.setUpdateTime(new Date());

        //联系人：每天都要上传记录
//        if(SysVariable.CONTACT.equals(type)) {
        	 Set<XUserContacts> contacts = riskInfo.getContacts();
             Set<XUserContacts> xUserContacts = new HashSet();
             //查询当天该用户有没有上传通讯录，如果上传了就不再重复上传了 
             int contactsCount = xUserContactsDao.findContactsByUserGid(gid, updateDate);
             if (contactsCount <= 0) {
                 if (contacts != null) {
                     if (contacts.size() > 0) {
                         contacts.forEach(u -> {
                             try {
                                 XUserContacts xUserContact = new XUserContacts(gid, updateDate, u.getContactName(), u.getPhoneNumber());
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
//        }
       
        //社交类app的个数-每天都要记录
//        if(SysVariable.APPNUM.equals(type)) {
        	XUserAppNum xUserAppNum=new XUserAppNum();
        	xUserAppNum.setAppNum(riskInfo.getAppNum());
        	xUserAppNum.setUpdateDate(updateDate);
        	xUserAppNum.setUserGid(gid);
        	
        	//查询当天是否已上传appNum，如果没有上传才能保存数据
        	Integer count=xUserAppNumDao.countAppNum(gid, updateDate);
        	if(count<=0 && !StringUtils.isEmpty(riskInfo.getAppNum())) {
        		xUserAppNumDao.saveAppNum(xUserAppNum);
        	}
        	
//        }
        
        //查询风控数据表有无该gid的用户，如果没有就新增一条记录
        Integer userNum = xUserRiskDao.findUser(gid);
        BigDecimal longitude = riskInfo.getLongitude();
        BigDecimal latitude = riskInfo.getLatitude();
        String macAddress=riskInfo.getMacAddress();
        String phoneBrand = riskInfo.getPhoneBrand();
        String trafficWay = riskInfo.getTrafficWay();
        xUserRisk.setLongitude(longitude);
    	xUserRisk.setLatitude(latitude);
        xUserRisk.setMacAddress(macAddress);
        xUserRisk.setPhoneBrand(phoneBrand);
        xUserRisk.setTrafficWay(trafficWay);
        
        //gps位置信息
//        if(SysVariable.GPS.equals(type)) {
            logger.info("风控表中已有的数据个数："+userNum+"-------------------");
        	//经纬度信息移动到风控表
            if(!StringUtils.isEmpty(longitude) && !StringUtils.isEmpty(latitude)) {
            	if(userNum == 0 || userNum==null) {
           		    logger.info("保存风控表数据gps："+userNum+"-------------------");
           		    xUserRiskDao.save(xUserRisk);
	           	}else {
	           		 logger.info("更新风控表数据gps："+userNum+"-------------------");
	           		 xUserRiskDao.updateGpsInfo(xUserRisk);
	           	}
            	
            }
        	
            
//        }
        
        //mac地址等其他信息
          userNum = xUserRiskDao.findUser(gid);
//        if(SysVariable.OTHER.equals(type)) {
            if(!StringUtils.isEmpty(macAddress) || !StringUtils.isEmpty(phoneBrand) || !StringUtils.isEmpty(trafficWay)) {
            	if(userNum == 0 || userNum==null) {
           		  logger.info("保存风控表数据other："+userNum+"-------------------");
           		  xUserRiskDao.save(xUserRisk);
           	 }else {
           		  logger.info("保存风控表数据other："+userNum+"-------------------");
           		  xUserRiskDao.updateOthers(xUserRisk);
           	  }
          }
        	
//        }
        
        return true;
    }
}
