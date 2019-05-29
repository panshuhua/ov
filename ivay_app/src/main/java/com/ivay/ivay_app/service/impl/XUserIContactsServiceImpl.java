package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dao.XUserContactsDao;
import com.ivay.ivay_app.model.XUserContacts;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_app.service.XUserContactsService;
import com.ivay.ivay_common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XUserIContactsServiceImpl implements XUserContactsService {
    @Autowired
    private XUserContactsDao xUserContactsDao;

    @Override
    public boolean saveContacts(String gid, List<XUserContacts> contacts) {
        String updateDate = DateUtils.getNowDateYYYY_MM_DD();
        List<XUserContacts> xUserContacts = new ArrayList<>();
        contacts.forEach(u -> {
            try{
                XUserContacts xUserContact = new XUserContacts(gid, updateDate, u.getContactName(), u.getPhoneNumber());
                xUserContacts.add(xUserContact);
                if(xUserContacts.size()>=200){
                    xUserContactsDao.insertBatchContacts(xUserContacts);
                    xUserContacts.clear();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            xUserContactsDao.insertBatchContacts(xUserContacts);
        });
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
        return new PageTableHandler(new PageTableHandler.CountHandler() {
            @Override
            public int count(PageTableRequest request) {
                return xUserContactsDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {
            @Override
            public List list(PageTableRequest request) {
                return xUserContactsDao.list(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }
}
