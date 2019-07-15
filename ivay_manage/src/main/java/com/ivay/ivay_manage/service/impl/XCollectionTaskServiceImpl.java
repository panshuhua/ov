package com.ivay.ivay_manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.enums.CollectionRepayStatusEnum;
import com.ivay.ivay_common.enums.CollectionStatusEnum;
import com.ivay.ivay_common.enums.OverDueLevelEnum;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionTaskService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.dao.master.XCollectionTaskDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.dto.CollectionTaskInfo;
import com.ivay.ivay_repository.dto.CollectionTaskLoanInfo;
import com.ivay.ivay_repository.dto.CollectionTaskResult;
import com.ivay.ivay_repository.model.XCollectionTask;
import com.ivay.ivay_repository.model.XRecordLoan;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
        try {
            logger.info("催收任務搜索，搜索條件{}", JSONObject.toJSONString(collectionTaskInfo));

            PageTableRequest request = new PageTableRequest();
            request.setLimit(limit);
            request.setOffset((num - 1) * limit);
            Map param = request.getParams();

            //根据条件搜索
            if (null != collectionTaskInfo) {
                param.put("name", collectionTaskInfo.getName());
                param.put("phone", collectionTaskInfo.getPhone());
                param.put("identityCard", collectionTaskInfo.getIdentityCard());
                //param.put("overdueLevel", collectionTaskInfo.getOverdueLevel());
                param.put("username", collectionTaskInfo.getUsername());
                param.put("collectionRepayStatus", collectionTaskInfo.getCollectionRepayStatus());
                param.put("collectionStatus", collectionTaskInfo.getCollectionStatus());

                changeLevelToTime(collectionTaskInfo, param);
            }
            request.setParams(param);

            List<CollectionTaskResult> collectionTaskResultList = xCollectionTaskDao.listByParams(request.getParams(), request.getOffset(), request.getLimit());
            //设置逾期级别
            collectionTaskResultList.forEach(o -> o.setOverdueLevel(OverDueLevelEnum.getLevelByDay(o.getOverdueDay())));

            return new PageTableHandler(
                    a -> xCollectionTaskDao.selectParamsListCount(a.getParams()),
                    a -> collectionTaskResultList
            ).handle(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int delete(Long id) {
        return xCollectionTaskDao.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCollectionTaskBatch() {
        logger.info("催收定時任務開始執行");

        //查询出过期没修改状态的订单
        List<XRecordLoan> recordLoanList = xRecordLoanDao.findOverdueOrder();

        //过滤掉已经插入的数据
        List<String> orderIdList = xCollectionTaskDao.selectOrderIds();
        recordLoanList = recordLoanList.stream().filter(o -> !orderIdList.contains(o.getOrderId())).collect(Collectors.toList());

        if (recordLoanList.size() > 0) {
            List<XCollectionTask> collectionTaskList = new ArrayList<>();

            recordLoanList.forEach(o -> {
                XCollectionTask xCollectionTask = new XCollectionTask();
                xCollectionTask.setOrderId(o.getOrderId());
                xCollectionTask.setDueCollectionAmount(o.getLoanAmount());
                xCollectionTask.setUserGid(o.getUserGid());
                xCollectionTask.setCollectionStatus(CollectionStatusEnum.WAITING_COLLECTION.getStatus());
                xCollectionTask.setCreateTime(new Date());
                xCollectionTask.setCollectionRepayStatus(CollectionRepayStatusEnum.OVERDUE.getStatus());
                xCollectionTask.setUpdateTime(xCollectionTask.getCreateTime());
                collectionTaskList.add(xCollectionTask);
            });

            xCollectionTaskDao.saveBatch(collectionTaskList);
        }
        logger.info("催收定時任務執行完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCollector(int collectorId, List<Integer> ids) {
        logger.info("催收任務{}，指派催收人{}", JSONObject.toJSONString(ids), collectorId);

        if (null == ids && ids.size() == 0) {
            throw new RuntimeException("参数ids不能为空！");
        }

        //查询对应的催单记录和还款状态等信息，判断是未指派还是重新指派
        List<CollectionTaskLoanInfo> results = xCollectionTaskDao.getCollectionsByIds(ids);
        //重新指派的集合
        List<XCollectionTask> assignList = new ArrayList<>();
        //未指派的集合
        List<XCollectionTask> notAssignList = new ArrayList<>();

        for (CollectionTaskLoanInfo collectionTaskLoanInfo: results) {
            //借款状态为 1打款成功 还款状态为 2已打款 还款时间已过时的
            if (collectionTaskLoanInfo.getLoanStatus() == 1 && collectionTaskLoanInfo.getRepaymentStatus() != 2 &&
                    collectionTaskLoanInfo.getDueTime().getTime() < System.currentTimeMillis()) {

                XCollectionTask collectionTask = new CollectionTaskLoanInfo();
                BeanUtils.copyProperties(collectionTaskLoanInfo,collectionTask);
                //首次指派
                if (collectionTask.getCollectionStatus() == CollectionStatusEnum.WAITING_COLLECTION.getStatus()) {
                    collectionTask.setCollectorId(collectorId);
                    collectionTask.setUpdateTime(new Date());
                    collectionTask.setCollectionRepayStatus(CollectionRepayStatusEnum.OVERDUE.getStatus());
                    collectionTask.setCollectionStatus(CollectionStatusEnum.ASSIGNED_COLLECTION.getStatus());

                    logger.info("催收任務{}，首次指派催收人{}", collectionTaskLoanInfo.getId(), collectorId);
                    //return xCollectionTaskDao.update(collectionTask) >= 1;
                    notAssignList.add(collectionTask);

                    //同一个人，无需重新指派
                } else if (collectionTask.getCollectorId() == collectorId) {
                    throw new BusinessException("0014", "该用户已经被指派，无需重新指派！");
                } else {
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

                    logger.info("催收任務{}，催收人{}，重新指派催收人{}", collectionTaskLoanInfo.getId(), collectionTask.getCollectorId(), collectorId);
                    assignList.add(newXCollectionTask);
                    //return xCollectionTaskDao.save(newXCollectionTask) >= 1;
                }
            }
        }

        //批量更新
        if (notAssignList.size() > 0) {
            xCollectionTaskDao.updateBatch(notAssignList);
        }

        //批量插入
        if (assignList.size() > 0) {
            xCollectionTaskDao.saveBatch(assignList);
        }
        return true;
    }

    @Override
    public PageTableResponse getCollectionListByUserGid(int limit, int num, CollectionTaskInfo collectionTaskInfo) {

        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);

        Map param = request.getParams();
        Long id = UserUtil.getLoginUser().getId();
        param.put("collectorId", id.intValue());

        if (null != collectionTaskInfo) {
            param.put("name", collectionTaskInfo.getName());
            param.put("phone", collectionTaskInfo.getPhone());
            param.put("identityCard", collectionTaskInfo.getIdentityCard());
            param.put("overdueLevel", collectionTaskInfo.getOverdueLevel());
            param.put("payDateStart", collectionTaskInfo.getPayDateStart());
            param.put("payDateEnd", collectionTaskInfo.getPayDateEnd());
            param.put("collectionDateStart", collectionTaskInfo.getCollectionDateStart());
            param.put("collectionDateEnd", collectionTaskInfo.getCollectionDateEnd());
            param.put("repayTimeStart", collectionTaskInfo.getRepayTimeStart());
            param.put("repayTimeEnd", collectionTaskInfo.getRepayTimeEnd());

            changeLevelToTime(collectionTaskInfo, param);
        }

        request.setParams(param);

        List<CollectionTaskResult> collectionTaskResultList = xCollectionTaskDao.getCollectionListByUserGid(request.getParams(), request.getOffset(), request.getLimit());
        //设置逾期级别
        collectionTaskResultList.forEach(o -> o.setOverdueLevel(OverDueLevelEnum.getLevelByDay(o.getOverdueDay())));

        return new PageTableHandler(
                a -> xCollectionTaskDao.getCollectionListByUserGidCount(a.getParams()),
                a -> collectionTaskResultList
        ).handle(request);
    }


    private void changeLevelToTime(CollectionTaskInfo collectionTaskInfo, Map param) {
        if (StringUtils.isNotBlank(collectionTaskInfo.getOverdueLevel())) {
            Integer day = OverDueLevelEnum.getDayByLevel(collectionTaskInfo.getOverdueLevel());
            //如果逾期等級為6+
            if (OverDueLevelEnum.OVERDUE_LEVEL_SIX_PLUS.getLevel().equals(collectionTaskInfo.getOverdueLevel())) {
                param.put("overdueDayMin", OverDueLevelEnum.OVERDUE_LEVEL_SIX.getDay());
            } else {
                param.put("overdueDayMin", OverDueLevelEnum.getDayByLevel(collectionTaskInfo.getOverdueLevel()) - 30);
            }
            param.put("overdueDayMax", OverDueLevelEnum.getDayByLevel(collectionTaskInfo.getOverdueLevel()));
        }
    }

    @Override
    public PageTableResponse getCollectionsRepayList(int limit, int num, CollectionTaskInfo collectionTaskInfo) {

        PageTableRequest request = new PageTableRequest();
        request.setLimit(limit);
        request.setOffset((num - 1) * limit);

        Map param = request.getParams();

        if (null != collectionTaskInfo) {
            param.put("name", collectionTaskInfo.getName());
            param.put("phone", collectionTaskInfo.getPhone());
            param.put("identityCard", collectionTaskInfo.getIdentityCard());
            param.put("overdueLevel", collectionTaskInfo.getOverdueLevel());
            param.put("repayTimeStart", collectionTaskInfo.getRepayTimeStart());
            param.put("repayTimeEnd", collectionTaskInfo.getRepayTimeEnd());

            changeLevelToTime(collectionTaskInfo, param);
        }
        request.setParams(param);

        List<CollectionTaskResult> collectionTaskResultList = xCollectionTaskDao.getCollectionsRepayList(request.getParams(), request.getOffset(), request.getLimit());
        //设置逾期级别
        collectionTaskResultList.forEach(o -> o.setOverdueLevel(OverDueLevelEnum.getLevelByDay(o.getOverdueDay())));
        int n = xCollectionTaskDao.getCollectionsRepayListCount(request.getParams());
        return new PageTableHandler(
                a -> xCollectionTaskDao.getCollectionsRepayListCount(a.getParams()),
                a -> collectionTaskResultList
        ).handle(request);
    }

    /**
     * 借款订单详情
     *
     * @param taskId
     * @return
     */
    @Override
    public XRecordLoan loanOrderInfo(long taskId) {
        return xCollectionTaskDao.loanOrderInfo(taskId);
    }

    /**
     * 还款详情
     *
     * @param taskId
     * @return
     */
    @Override
    public PageTableResponse repaymentInfo(long taskId) {
        return new PageTableResponse<>(xCollectionTaskDao.repaymentInfo(taskId));
    }

}
