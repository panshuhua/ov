package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dto.RiskDataType;
import com.ivay.ivay_app.model.RiskInfo;
import com.ivay.ivay_app.service.XUserContactsService;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_repository.dao.master.XUserAppNumDao;
import com.ivay.ivay_repository.dao.master.XUserContactsDao;
import com.ivay.ivay_repository.dao.master.XUserRiskDao;
import com.ivay.ivay_repository.model.XUserAppNum;
import com.ivay.ivay_repository.model.XUserContacts;
import com.ivay.ivay_repository.model.XUserInfo;
import com.ivay.ivay_repository.model.XUserRisk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class XUserIContactsServiceImpl implements XUserContactsService {
    @Autowired
    private XUserContactsDao xUserContactsDao;
    @Autowired
    private XUserAppNumDao xUserAppNumDao;
    @Autowired
    private XUserRiskDao xUserRiskDao;

    @Override
    public boolean saveAll(RiskInfo riskInfo) {
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
        if(RiskDataType.CONTACT.equals(type)) {
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
        }
       
        //社交类app的个数-每天都要记录
        if(RiskDataType.APPNUM.equals(type)) {
        	xUserInfo.setAppNum(riskInfo.getAppNum());
        	XUserAppNum xUserAppNum=new XUserAppNum();
        	xUserAppNum.setAppNum(riskInfo.getAppNum());
        	xUserAppNum.setUpdateDate(updateDate);
        	xUserAppNum.setUserGid(gid);
        	
        	//查询当天是否已上传appNum，如果没有上传才能保存数据
        	Integer count=xUserAppNumDao.countAppNum(gid, updateDate);
        	if(count<=0 && !StringUtils.isEmpty(riskInfo.getAppNum())) {
        		xUserAppNumDao.saveAppNum(xUserAppNum);
        	}
        	
        }
        
        //查询风控数据表有无该gid的用户，如果没有就新增一条记录
        Integer userNum = xUserRiskDao.findUser(gid);
        xUserRisk.setLongitude(riskInfo.getLongitude());
    	xUserRisk.setLatitude(riskInfo.getLatitude());
        xUserRisk.setMacAddress(riskInfo.getMacAddress());
        xUserRisk.setPhoneBrand(riskInfo.getPhoneBrand());
        xUserRisk.setTrafficWay(riskInfo.getTrafficWay());
        
        //gps位置信息
        if(RiskDataType.GPS.equals(type)) {
        	//xUserInfo.setLongitude(riskInfo.getLongitude());
            //xUserInfo.setLatitude(riskInfo.getLatitude());
            //xUserInfoDao.updateGps(xUserInfo);
        	
        	//经纬度信息移动到风控表
        	if(userNum == 0) {
        		xUserRiskDao.save(xUserRisk);
        	}else {
        		xUserRiskDao.updateGpsInfo(xUserRisk);
        	}
            
        }
        
        //mac地址等其他信息
        if(RiskDataType.OTHER.equals(type)) {
        	if(userNum == 0) {
        		xUserRiskDao.save(xUserRisk);
        	}else {
        		xUserRiskDao.updateOthers(xUserRisk);
        	}
        }
        
        return true;
    }

    @Override
    public PageTableResponse getByGid(Integer limit, Integer num, String gid) {
        PageTableRequest request = new PageTableRequest();
        request.setOffset(limit * (num - 1));
        request.setLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "contact_name");
        params.put("userGid", gid);
        request.setParams(params);
        return new PageTableHandler(
                a -> xUserContactsDao.count(a.getParams()),
                a -> xUserContactsDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }
}
