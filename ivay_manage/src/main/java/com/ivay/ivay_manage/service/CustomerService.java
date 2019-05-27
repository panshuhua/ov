package com.ivay.ivay_manage.service;

import com.ivay.ivay_manage.model.*;

import java.util.List;
import java.util.Map;

public interface CustomerService {
	
   XFileInfo queryphotoUrl(String id);
   
   int countBasicInfo(Map<String, Object> params);

   List<XUserInfo> listBasicInfo(Map<String, Object> params, Integer offset, Integer limit);
   
   int countContactInfo(Map<String, Object> params);

   List<XUserExtInfo> listContactInfo(Map<String, Object> params, Integer offset, Integer limit);
   
   int countLoan(Map<String, Object> params);

   List<XRecordLoan> listLoan(Map<String, Object> params, Integer offset, Integer limit);
   
   int countRepay(Map<String, Object> params);

   List<XRecordRepayment> listRepay(Map<String, Object> params, Integer offset, Integer limit);
   
   int countBank(Map<String, Object> params);

   List<XUserBankcoadInfo> listBank(Map<String, Object> params, Integer offset, Integer limit);
   
}
