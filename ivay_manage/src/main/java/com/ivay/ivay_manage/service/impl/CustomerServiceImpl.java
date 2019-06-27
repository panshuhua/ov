package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_manage.service.CustomerService;
import com.ivay.ivay_repository.dao.master.CustomerDao;
import com.ivay.ivay_repository.dao.master.XRecordRepaymentDao;
import com.ivay.ivay_repository.dao.master.XUserContactsDao;
import com.ivay.ivay_repository.dto.XRecordLoan2;
import com.ivay.ivay_repository.dto.XRecordRepayment2;
import com.ivay.ivay_repository.dto.XUserCardAndBankInfo;
import com.ivay.ivay_repository.model.XConfig;
import com.ivay.ivay_repository.model.XFileInfo;
import com.ivay.ivay_repository.model.XUserExtInfo;
import com.ivay.ivay_repository.model.XUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerDao customerDao;

    @Override
    public XFileInfo queryphotoUrl(String id) {
        return customerDao.queryphotoUrl(id);
    }

    @Override
    public int countBasicInfo(Map<String, Object> params) {
        return customerDao.countBasicInfo(params);
    }

    @Override
    public List<XUserInfo> listBasicInfo(Map<String, Object> params, Integer offset, Integer limit) {
        List<XUserInfo> list = customerDao.listBasicInfo(params, offset, limit);
        String lang = (String) params.get("lang");
        for (XUserInfo xUserInfo : list) {
            // 用户状态
            String userStatus = xUserInfo.getUserStatus();
            XConfig xConfig = customerDao.findConfigByType(SysVariable.TEMPLATE_USER_STATUS, lang);
            Map map = JsonUtils.jsonToMap(xConfig.getContent());
            userStatus = (String) map.get(userStatus);
            xUserInfo.setUserStatus(userStatus);

            // 性别
            String sex = xUserInfo.getSex();
            xConfig = customerDao.findConfigByType(SysVariable.TEMPLATE_SEX, lang);
            map = JsonUtils.jsonToMap(xConfig.getContent());
            sex = (String) map.get(sex);
            xUserInfo.setSex(sex);

            // 学历
            String education = xUserInfo.getEducation();
            xConfig = customerDao.findConfigByType(SysVariable.TEMPLATE_EDUCATION, lang);
            map = JsonUtils.jsonToMap(xConfig.getContent());
            education = (String) map.get(education);
            xUserInfo.setEducation(education);

            // 婚姻状况
            String marital = xUserInfo.getMarital();
            xConfig = customerDao.findConfigByType(SysVariable.TEMPLATE_MARITAL, lang);
            map = JsonUtils.jsonToMap(xConfig.getContent());
            marital = (String) map.get(marital);
            xUserInfo.setMarital(marital);
        }
        return list;
    }

    @Override
    public int countContactInfo(Map<String, Object> params) {
        return customerDao.countContactInfo(params);
    }

    @Override
    public List<XUserExtInfo> listContactInfo(Map<String, Object> params, Integer offset, Integer limit) {
        List<XUserExtInfo> list = customerDao.listContactInfo(params, offset, limit);
        String lang = (String) params.get("lang");
        for (XUserExtInfo xUserExtInfo : list) {
            String majorRelationship = xUserExtInfo.getMajorRelationship();
            XConfig xConfig = customerDao.findConfigByType(SysVariable.TEMPLATE_RELATION, lang);
            Map map = JsonUtils.jsonToMap(xConfig.getContent());
            majorRelationship = (String) map.get(majorRelationship);
            xUserExtInfo.setMajorRelationship(majorRelationship);

            String bakRelationship = xUserExtInfo.getBakRelationship();
            xConfig = customerDao.findConfigByType(SysVariable.TEMPLATE_RELATION, lang);
            map = JsonUtils.jsonToMap(xConfig.getContent());
            bakRelationship = (String) map.get(bakRelationship);
            xUserExtInfo.setBakRelationship(bakRelationship);

            // 各种照片的路径
            String photo1UrlId = xUserExtInfo.getPhoto1Url();
            XFileInfo xFileInfo = customerDao.queryphotoUrl(photo1UrlId);
            if (xFileInfo != null) {
                xUserExtInfo.setPhoto1Url(xFileInfo.getUrl());
            }

            String photo2UrlId = xUserExtInfo.getPhoto2Url();
            xFileInfo = customerDao.queryphotoUrl(photo2UrlId);
            if (xFileInfo != null) {
                xUserExtInfo.setPhoto2Url(xFileInfo.getUrl());
            }

            String photo3UrlId = xUserExtInfo.getPhoto3Url();
            xFileInfo = customerDao.queryphotoUrl(photo3UrlId);
            if (xFileInfo != null) {
                xUserExtInfo.setPhoto3Url(xFileInfo.getUrl());
            }

        }

        return list;

    }

    @Override
    public int countLoan(Map<String, Object> params) {
        return customerDao.countLoan(params);
    }

    @Override
    public List<XRecordLoan2> listLoan(Map<String, Object> params, Integer offset, Integer limit) {
        List<XRecordLoan2> list = customerDao.listLoan(params, offset, limit);
        String lang = (String) params.get("lang");
        for (XRecordLoan2 xRecordLoan : list) {
            String loanStatus = xRecordLoan.getLoanStatus();
            XConfig xConfig = customerDao.findConfigByType(SysVariable.TEMPLATE_LOAN_STATUS, lang);
            Map map = JsonUtils.jsonToMap(xConfig.getContent());
            loanStatus = (String) map.get(loanStatus);
            xRecordLoan.setLoanStatus(loanStatus);
        }
        return list;
    }

    @Override
    public int countRepay(Map<String, Object> params) {
        return customerDao.countRepay(params);
    }

    @Override
    public List<XRecordRepayment2> listRepay(Map<String, Object> params, Integer offset, Integer limit) {
        List<XRecordRepayment2> list = customerDao.listRepay(params, offset, limit);
        String lang = (String) params.get("lang");
        for (XRecordRepayment2 xRecordRepayment : list) {
            String repaymentStatus = xRecordRepayment.getRepaymentStatus();
            XConfig xConfig = customerDao.findConfigByType(SysVariable.TEMPLATE_REPAYMENT_STATUS, lang);
            Map map = JsonUtils.jsonToMap(xConfig.getContent());
            repaymentStatus = (String) map.get(repaymentStatus);
            xRecordRepayment.setRepaymentStatus(repaymentStatus);
        }

        return list;
    }

    @Override
    public int countBank(Map<String, Object> params) {
        return customerDao.countBank(params);
    }

    @Override
    public List<XUserCardAndBankInfo> listBank(Map<String, Object> params, Integer offset, Integer limit) {
        return customerDao.listBank(params, offset, limit);
    }

    @Autowired
    private XUserContactsDao xUserContactsDao;

    /**
     * 获取通讯录
     *
     * @param limit
     * @param num
     * @param userGid
     * @return
     */
    @Override
    public PageTableResponse list(int limit, int num, String userGid) {
        PageTableRequest request = new PageTableRequest();
        request.setOffset((num - 1) * limit);
        request.setLimit(limit);
        request.getParams().put("userGid", userGid);
        return new PageTableHandler(
                a -> xUserContactsDao.count(a.getParams()),
                a -> xUserContactsDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Autowired
    private XRecordRepaymentDao xRecordRepaymentDao;

    @Override
    public PageTableResponse repaymentInfo(int limit, int num, String userGid, String type) {
        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);
        // type 0 逾期天数为(0,3], 1 逾期天数为 3天以上
        request.getParams().put("type", type);
        request.getParams().put("userGid", userGid);
        return new PageTableHandler(a -> xRecordRepaymentDao.countRepaymentInfo(a.getParams()),
                a -> xRecordRepaymentDao.listRepaymentInfo(a.getParams(), a.getOffset(), a.getLimit())).handle(request);
    }
}
