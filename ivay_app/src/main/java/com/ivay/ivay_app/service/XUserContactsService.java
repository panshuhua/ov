package com.ivay.ivay_app.service;

import com.ivay.ivay_app.model.XUserContacts;
import com.ivay.ivay_app.table.PageTableResponse;

import java.util.List;

public interface XUserContactsService {

    /**
     * 根据gid添加联系人
     *
     * @param gid
     * @return
     */
    boolean saveContacts(String gid, List<XUserContacts> contacts);

    PageTableResponse getByGid(Integer limit, Integer num, String gid);
}
