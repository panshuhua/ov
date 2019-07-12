package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.enums.CollectionStatusEnum;
import com.ivay.ivay_common.enums.OverDueLevelEnum;
import com.ivay.ivay_common.enums.CollectionRepayStatusEnum;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionTaskService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.XCollectionTaskDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dto.CollectionTaskInfo;
import com.ivay.ivay_repository.dto.CollectionTaskResult;
import com.ivay.ivay_repository.model.XCollectionTask;
import com.ivay.ivay_repository.model.XRecordLoan;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class XCollectionTaskServiceImpl implements XCollectionTaskService {
    private static final Logger logger = LoggerFactory.getLogger(XCollectionTaskService.class);

    @Autowired
    private XCollectionTaskDao xCollectionTaskDao;
    @Autowired
    private XRecordLoanDao xRecordLoanDao;

    @Override
    public XCollectionTask save(XCollectionTask xCollectionTask) {
        return xCollectionTaskDao.save(xCollectionTask) == 1 ? xCollectionTask : null;
    }

    @Override
    public XCollectionTask get(Integer id) {
        return xCollectionTaskDao.getById(id);
    }

    @Override
    public XCollectionTask update(XCollectionTask xCollectionTask) {
        return xCollectionTaskDao.update(xCollectionTask) == 1 ? xCollectionTask : null;
    }

    @Override
    public PageTableResponse list(int limit, int num, CollectionTaskInfo collectionTaskInfo) {

        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);
        Map param = request.getParams();

        //根据条件搜索
        if(null != collectionTaskInfo) {
            param.put("name", collectionTaskInfo.getName());
            param.put("phone", collectionTaskInfo.getPhone());
            param.put("identityCard", collectionTaskInfo.getIdentityCard());
            param.put("overdueLevel", collectionTaskInfo.getOverdueLevel());
            param.put("username", collectionTaskInfo.getUsername());
            param.put("collectionRepayStatus", collectionTaskInfo.getCollectionRepayStatus());
            param.put("collectionStatus", collectionTaskInfo.getCollectionStatus());

            if(StringUtils.isNotBlank(collectionTaskInfo.getOverdueLevel())){
                Integer day = OverDueLevelEnum.getDayByLevel(collectionTaskInfo.getOverdueLevel());
                //如果逾期等級為6+
                if(OverDueLevelEnum.OVERDUE_LEVEL_SIX_PLUS.getLevel().equals(collectionTaskInfo.getOverdueLevel())){
                    param.put("overdueDayMin", OverDueLevelEnum.OVERDUE_LEVEL_SIX.getDay());
                }else{
                    param.put("overdueDayMin", OverDueLevelEnum.getDayByLevel(collectionTaskInfo.getOverdueLevel()) - 30);
                }
                param.put("overdueDayMax", OverDueLevelEnum.getDayByLevel(collectionTaskInfo.getOverdueLevel()));
            }
        }
        request.setParams(param);

        List<CollectionTaskResult> collectionTaskResultList = xCollectionTaskDao.listByParams(request.getParams(), request.getOffset(), request.getLimit());
        //设置逾期级别
        collectionTaskResultList.forEach(o -> o.setOverdueLevel(OverDueLevelEnum.getLevelByDay(o.getOverdueDay())));

        return new PageTableHandler(
                a -> xCollectionTaskDao.selectParamsListCount(a.getParams()),
                a -> collectionTaskResultList
        ).handle(request);
    }

    @Override
    public int delete(Long id) {
        return xCollectionTaskDao.delete(id);
    }

    @Override
    @Transactional
    public void saveCollectionTaskBatch() {
        //查询出过期没修改状态的订单
        List<XRecordLoan> recordLoanList = xRecordLoanDao.findOverdueOrder();

        //修改已经过期的订单状态
        /*recordLoanList.forEach(o -> {
            o.setRepaymentStatus(CollectionRepayStatusEnum.OVERDUE.getStatus());
            o.setUpdateTime(new Date());
                });
        xRecordLoanDao.updateByBatch(recordLoanList);*/

        //过滤掉已经插入的数据
        List<String> orderIdList = xCollectionTaskDao.selectOrderIds();
        recordLoanList = recordLoanList.stream().filter(o -> !orderIdList.contains(o.getOrderId())).collect(Collectors.toList());

        if(recordLoanList.size() > 0) {
            List<XCollectionTask> collectionTaskList = new ArrayList<>();

            recordLoanList.forEach(o -> {
                XCollectionTask xCollectionTask = new XCollectionTask();
                xCollectionTask.setOrderId(o.getOrderId());
                xCollectionTask.setDueCollectionAmount(o.getLoanAmount());
                xCollectionTask.setUserGid(o.getUserGid());
                xCollectionTask.setCollectionStatus(CollectionStatusEnum.WAITING_COLLECTION.getStatus().byteValue());
                xCollectionTask.setCreateTime(new Date());
                xCollectionTask.setUpdateTime(o.getCreateTime());
                collectionTaskList.add(xCollectionTask);
            });

            xCollectionTaskDao.saveBatch(collectionTaskList);
        }
    }

    @Override
    @Transactional
    public boolean updateCollector(Integer collectorId, Integer id) {
        //判断是否重新指派，否-直接修改记录 是-重新生成订单记录
        XCollectionTask collectionTask = xCollectionTaskDao.getById(id);
        //查询该指派任务对应的还款信息
        XRecordLoan recordLoan = xRecordLoanDao.getXRecordLoanByOrderId(collectionTask.getOrderId());

        //判断是否有逾期未还的借款
        if(recordLoan.getLoanStatus() == 1 &&
                recordLoan.getRepaymentStatus() != CollectionRepayStatusEnum.FINISHED_REPAY.getStatus() &&
                recordLoan.getDueTime().getTime() < System.currentTimeMillis() &&
                recordLoan.getDueAmount() + recordLoan.getOverdueFee() > 0){

            //首次指派
            if(collectionTask.getCollectionStatus() == CollectionStatusEnum.WAITING_COLLECTION.getStatus()){
                collectionTask.setCollectorId(collectorId);
                collectionTask.setUpdateTime(new Date());
                collectionTask.setCollectionRepayStatus(CollectionRepayStatusEnum.OVERDUE.getStatus());
                collectionTask.setCollectionStatus(CollectionStatusEnum.ASSIGNED_COLLECTION.getStatus());

                return xCollectionTaskDao.update(collectionTask) >= 1;

                //同一个人，无需重新指派
            }else if(collectionTask.getCollectorId() == collectorId){
                throw new BusinessException("0014", "该用户已经被指派，无需重新指派！");
            }else{
                //先修改催收进度，再重新生成记录
                collectionTask.setCollectionStatus(CollectionStatusEnum.FINISH_COLLECTION.getStatus());
                collectionTask.setUpdateTime(new Date());
                xCollectionTaskDao.update(collectionTask);

                XCollectionTask newXCollectionTask = new XCollectionTask();
                newXCollectionTask.setOrderId(collectionTask.getOrderId());
                newXCollectionTask.setUserGid(collectionTask.getUserGid());
                newXCollectionTask.setCollectorId(collectorId);
                newXCollectionTask.setCollectionStatus(CollectionStatusEnum.ASSIGNED_COLLECTION.getStatus());
                newXCollectionTask.setDueCollectionAmount(collectionTask.getDueCollectionAmount());
                newXCollectionTask.setCollectionRepayStatus(collectionTask.getCollectionRepayStatus());
                newXCollectionTask.setCollectionAmount(0L);
                newXCollectionTask.setCollectionOverdueFee(0L);
                newXCollectionTask.setCreateTime(new Date());
                newXCollectionTask.setUpdateTime(new Date());

                return xCollectionTaskDao.save(newXCollectionTask) >= 1;
            }

        }
        return false;
    }

    @Override
    public PageTableResponse getCollectionListByUserGid(int limit, int num) {
        Long id = UserUtil.getLoginUser().getId();

        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);
        Map param = request.getParams();
        param.put("collectorId", id.intValue());
        request.setParams(param);

        List<CollectionTaskResult> collectionTaskResultList = xCollectionTaskDao.getCollectionListByUserGid(request.getParams(), request.getOffset(), request.getLimit());
        //设置逾期级别
        collectionTaskResultList.forEach(o -> o.setOverdueLevel(OverDueLevelEnum.getLevelByDay(o.getOverdueDay())));

        return new PageTableHandler(
                a -> xCollectionTaskDao.getCollectionListByUserGidCount(a.getParams()),
                a -> xCollectionTaskDao.getCollectionListByUserGid(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

}
