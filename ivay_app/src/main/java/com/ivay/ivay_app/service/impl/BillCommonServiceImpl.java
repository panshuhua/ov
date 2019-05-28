package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.service.BillCommonService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_repository.dao.master.TokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillCommonServiceImpl implements BillCommonService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    private static final String DD = "DD";

    @Autowired
    private TokenDao userDao;

    @Override
    public String getBillNo() {
        String no = userDao.getBillNo();
        return DD + DateUtils.getNowDateYYYYMMDD() + StringUtil.autoGenericCode(no, 6);

    }


}
