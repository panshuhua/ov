package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.BlackUserService;
import com.ivay.ivay_repository.dao.risk.BlackUserDao;
import com.ivay.ivay_repository.model.BlackUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlackUserServiceImpl implements BlackUserService {
    private static final Logger logger = LoggerFactory.getLogger(BlackUserService.class);

    @Autowired
    private BlackUserDao blackUserDao;

    @Override
    public BlackUser save(BlackUser blackUser) {
        return blackUserDao.save(blackUser) == 1 ? blackUser : null;
    }

    @Override
    public BlackUser get(Long id) {
        return blackUserDao.getById(id);
    }

    @Override
    public BlackUser update(BlackUser blackUser) {
        return blackUserDao.update(blackUser) == 1 ? blackUser : null;
    }

    @Override
    public PageTableResponse list(PageTableRequest request) {
        return new PageTableHandler(
                a -> blackUserDao.count(a.getParams()),
                a -> blackUserDao.list(a.getParams(), a.getOffset(), a.getLimit())
        ).handle(request);
    }

    @Override
    public int delete(Long id) {
        return blackUserDao.delete(id);
    }

    /**
     * 根据电话号码 或身份证判断是否是黑名单用户
     *
     * @param phone
     * @param identityCard
     * @return
     */
    @Override
    public boolean isBlackUser(String phone, String identityCard) {
        if (StringUtils.isEmpty(phone) && StringUtils.isEmpty(identityCard)) {
            logger.info("身份证与电话号码不能同时为空");
            return false;
        }
        List<BlackUser> list = blackUserDao.selectBlackUsers(phone, identityCard);
        if (list.size() > 0) {
            logger.info("{},{}是黑名单用户", phone, identityCard);
            return true;
        }
        return false;
    }
}
