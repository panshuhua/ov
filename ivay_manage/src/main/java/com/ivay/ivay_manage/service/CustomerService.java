package com.ivay.ivay_manage.service;

import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.dto.CustomerInfo;
import com.ivay.ivay_repository.dto.XRecordLoan2;
import com.ivay.ivay_repository.dto.XRecordRepayment2;
import com.ivay.ivay_repository.dto.XUserCardAndBankInfo;
import com.ivay.ivay_repository.model.XFileInfo;
import com.ivay.ivay_repository.model.XUserExtInfo;
import com.ivay.ivay_repository.model.XUserInfo;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    XFileInfo queryphotoUrl(String id);

    int countBasicInfo(Map<String, Object> params);

    PageTableResponse listBasicInfo(int limit, int num, CustomerInfo customerInfo);

    int countContactInfo(Map<String, Object> params);

    PageTableResponse listContactInfo(String userGid, Integer num, Integer limit);

    int countLoan(Map<String, Object> params);

    PageTableResponse listLoan(String userGid, int num, int limit);

    int countRepay(Map<String, Object> params);

    PageTableResponse listRepay(String userGid, int num, int limit);

    int countBank(Map<String, Object> params);

    PageTableResponse listBank(String userGid, int num, int limit);

    /**
     * 获取通讯录
     *
     * @param limit
     * @param num
     * @param userGid
     * @return
     */
    PageTableResponse list(int limit, int num, String userGid);

    PageTableResponse repaymentInfo(int limit, int num, String userGid, String type);

}
