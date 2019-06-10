package com.ivay.ivay_manage.job;

import com.ivay.ivay_manage.service.ThreadPoolService;
import com.ivay.ivay_manage.service.XUserInfoService;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import com.ivay.ivay_repository.dao.risk.RiskUserDao;
import com.ivay.ivay_repository.model.XUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//@Component
//@Configuration
//@EnableScheduling
public class BackgroundScheduleTask {
    private static final Logger logger = LoggerFactory.getLogger(BackgroundScheduleTask.class);

    @Autowired
    private XUserInfoDao xUserInfoDao;

    @Autowired
    private RiskUserDao riskUserDao;

    @Autowired
    private ThreadPoolService threadPoolService;

    @Autowired
    private XUserInfoService xUserInfoService;

//    @Scheduled(cron = "${timer.autoAudit}")
    private void autoAudit() {
        logger.info("开始执行自动审核...");
        List<XUserInfo> list = xUserInfoDao.toBeAuditedList(1);
        StringBuilder sb = new StringBuilder();
        if (list.size() > 0) {
            // 查出所有待审核用户
            for (XUserInfo xui : list) {
                if (!StringUtils.isEmpty(sb)) {
                    sb.append(",");
                }
                sb.append(xui.getPhone());
            }
            logger.info("三天内的待审核用户: " + sb.toString());
            String phones = riskUserDao.selectPhoneByBatch(sb.toString().split(","));
            if (!StringUtils.isEmpty(phones)) {
                // 白名单用户
                logger.info("白名单用户: " + phones);
                List<String> phoneList = new ArrayList<>();
                Collections.addAll(phoneList, phones.split(","));
                sb = new StringBuilder();
                for (XUserInfo xui : list) {
                    if (phoneList.contains(xui.getPhone())) {
                        if (!StringUtils.isEmpty(sb)) {
                            sb.append(",");
                        }
                        sb.append(xui.getUserGid());
                    }
                }
                logger.info("白名单gid：" + sb.toString());
                for (String gid : sb.toString().split(",")) {
                    threadPoolService.execute(() -> xUserInfoService.auditUpdate(gid, 1, null, null));
                }
            }
        }
        logger.info("自动审核结束...");
    }
}
