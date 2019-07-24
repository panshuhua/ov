package com.ivay.ivay_app.service.impl;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ivay.ivay_app.service.XAppVersionUpdateService;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dao.master.XVersionUpdateDao;
import com.ivay.ivay_repository.model.XVersionUpdate;

// 比如发布一个新版 1.0.5 标记最小1.0.3的版本号会用热更新去更新
// 客户端传来当前版本 1.0.1 对比1.0.3 小于1.0.3 返回true 需要更新
// 客户端传来当前版本 1.0.4 对比1.0.3 大于 返回false
// 规定版本号为 *.*.**
@Service
public class XAppVersionUpdateImpl implements XAppVersionUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(XAppVersionUpdateImpl.class);
    @Autowired
    XVersionUpdateDao xVersionUpdateDao;
    // app热更新最小版本号
    @Value("${minAppVersionNumber}")
    private String minAppVersionNumber;

    @Override
    public int save(XVersionUpdate xVersionUpdate) {
        return xVersionUpdateDao.save(xVersionUpdate);
    }

    @Override
    public XVersionUpdate findUpdate(String versionNumber) {
        XVersionUpdate versionUpdate = xVersionUpdateDao.findUpdate();
        logger.info("当前数据库中最新的版本信息：" + versionUpdate);

        if (versionUpdate != null) {
            boolean isUpdate = checkUpdate(versionNumber, versionUpdate.getVersionNumber());
            logger.info("是否需要更新版本：{}", isUpdate);
            if (isUpdate) {
                // 2-需要强制更新版本
                if (SysVariable.NEED_FORCE_UPDATE.equals(versionUpdate.getNeedUpdate())) {
                    versionUpdate.setNeedUpdate(SysVariable.NEED_FORCE_UPDATE);
                } else {
                    versionUpdate.setNeedUpdate(SysVariable.NEED_NORMAL_UPDATE);
                }
            } else {
                versionUpdate.setNeedUpdate(SysVariable.NEED_NO_UPDATE);
            }

            return versionUpdate;

        }

        return null;
    }

    // true表示需要版本更新
    public boolean checkUpdate(String version, String currVersion) {
        // 数据库查最新的一条版本记录
        // currVersion = "1.0.5"; // 当前最新的版本
        // String currMinVersion = "1.0.3";
        String currMinVersion = minAppVersionNumber; // 1.0.3和之后的版本会使用热更新 所以不用更新提示

        version = version.trim();

        // version = version.substring(0, version.lastIndexOf("-"));

        // 传来版本号不合法
        boolean isExpect = Pattern.matches("^[-\\+]?[.\\d]*$", version);

        if (!isExpect) {
            return false;
        }

        // 等于最新版本
        if (version.equals(currVersion)) {
            return false;
        }

        if (versionToNum(version) < versionToNum(currMinVersion != null ? currMinVersion : currVersion)) {
            return true;
        } else {
            return false;
        }

    }

    // 版本号转数字
    public int versionToNum(String version) {
        int num = 0;
        int index = 3;
        String[] sps = version.split("\\.");
        for (String retval : sps) {
            int i = Integer.parseInt(retval);
            num += i * Math.pow(i == 1 ? 1 : 10, index);
            index -= 1;
        }
        return num;
    }

    public static void main(String[] args) {
        XAppVersionUpdateImpl update = new XAppVersionUpdateImpl();
        String version = "1.0.1-v1";
        System.out.println(update.checkUpdate(version, null));
    }

}
