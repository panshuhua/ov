package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.model.RiskInfo;
import com.ivay.ivay_app.service.XUserContactsService;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_repository.dao.master.XUserContactsDao;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.model.XUserContacts;
import com.ivay.ivay_repository.model.XUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class XUserIContactsServiceImpl implements XUserContactsService {
    @Autowired
    private XUserContactsDao xUserContactsDao;
    @Autowired
    private XUserInfoDao xUserInfoDao;

    @Override
    public boolean saveAll(RiskInfo riskInfo) {
        Set<XUserContacts> contacts = riskInfo.getContacts();
        String gid = riskInfo.getUserGid();
        XUserInfo xUserInfo = new XUserInfo();
        xUserInfo.setUserGid(gid);
        xUserInfo.setLongitude(riskInfo.getLongitude());
        xUserInfo.setLatitude(riskInfo.getLatitude());
        xUserInfo.setAppNum(riskInfo.getAppNum());
        xUserInfo.setUpdateTime(new Date());

        String updateDate = DateUtils.getNowDateYYYY_MM_DD();
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

        xUserInfoDao.updateGpsAppNum(xUserInfo);

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
