package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XSalesRecordService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.XRiskUserDao;
import com.ivay.ivay_repository.dao.master.XSalesRecordDao;
import com.ivay.ivay_repository.model.RiskUser;
import com.ivay.ivay_repository.model.XSalesRecord;
import org.omg.CORBA.portable.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service
public class XSalesRecordServiceImpl implements XSalesRecordService {
    private static final Logger logger = LoggerFactory.getLogger(XSalesRecordService.class);

    @Autowired
    private XSalesRecordDao xSalesRecordDao;
    @Autowired
    private XRiskUserDao riskUserDao;

    @Override
    @Transactional
    public Boolean add(XSalesRecord xSalesRecord) {

        long id = UserUtil.getLoginUser().getId();
        RiskUser riskUser = riskUserDao.getById(xSalesRecord.getRiskUserId());

        if (null == riskUser || riskUser.getSalesmanId() != id){
            throw new BusinessException("您没有权限添加销售记录！");
        }
        xSalesRecord.setSalesmanId((int)id);
        xSalesRecord.setCreateTime(new Date());
        xSalesRecord.setUpdateTime(new Date());

        int n = xSalesRecordDao.insert(xSalesRecord);

        //修改状态为已处理
        riskUser.setDealStatus(1);
        riskUserDao.updateById(riskUser);

        return n ==1 ? true : false;
    }

    @Override
    public XSalesRecord get(Long id) {
        return xSalesRecordDao.getById(id);
    }

    @Override
    public XSalesRecord update(XSalesRecord xSalesRecord) {
        return xSalesRecordDao.update(xSalesRecord) == 1 ? xSalesRecord : null;
    }

    @Override
    public PageTableResponse list(PageTableRequest request) {
        return new PageTableHandler(
                a -> xSalesRecordDao.count(a.getParams()),
                a -> xSalesRecordDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public int delete(Long id) {
        return xSalesRecordDao.delete(id);
    }

    @Override
    public PageTableResponse getSalesRecordList(int limit, int num, int id) {

        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);

        return new PageTableHandler(
                a -> xSalesRecordDao.getSalesRecordListCount(id),
                a -> xSalesRecordDao.getSalesRecordList(id, a.getOffset(), a.getLimit())
        ).handle(request);
    }
}
