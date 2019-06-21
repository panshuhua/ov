package com.ivay.ivay_manage.service;

import com.ivay.ivay_repository.dto.XRecordLoan2;
import com.ivay.ivay_repository.dto.XRecordRepayment2;
import com.ivay.ivay_repository.model.XFileInfo;
import com.ivay.ivay_repository.dto.XUserCardAndBankInfo;
import com.ivay.ivay_repository.model.XUserExtInfo;
import com.ivay.ivay_repository.model.XUserInfo;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    XFileInfo queryphotoUrl(String id);

    int countBasicInfo(Map<String, Object> params);

    List<XUserInfo> listBasicInfo(Map<String, Object> params, Integer offset, Integer limit);

    int countContactInfo(Map<String, Object> params);

    List<XUserExtInfo> listContactInfo(Map<String, Object> params, Integer offset, Integer limit);

    int countLoan(Map<String, Object> params);

    List<XRecordLoan2> listLoan(Map<String, Object> params, Integer offset, Integer limit);

    int countRepay(Map<String, Object> params);

    List<XRecordRepayment2> listRepay(Map<String, Object> params, Integer offset, Integer limit);

    int countBank(Map<String, Object> params);

    List<XUserCardAndBankInfo> listBank(Map<String, Object> params, Integer offset, Integer limit);

}
