package com.ivay.ivay_repository.utils;

import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.dto.XUserCardAndBankInfo;
import com.ivay.ivay_repository.model.XUserInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * 非必要信息不对前端展示
 *
 * @author Anime
 */
public class DesensitizationUtil {
    /**
     * 用户信息处理类
     *
     * @param user
     * @return
     */
    public static XUserInfo UserInfoDesensitization(XUserInfo user) {
        user.setPassword(null);
        user.setTransPwd(null);
        return user;
    }

    public static void BankCardDesensitization(List<XUserCardAndBankInfo> list) {
        for (Iterator<XUserCardAndBankInfo> it = list.iterator(); it.hasNext(); ) {
            XUserCardAndBankInfo cb = it.next();
            cb.setCardNo(StringUtil.phoneDesensitization(cb.getCardNo()));
        }
    }

    public static String refuseReason(String type, String reason) {
        if (StringUtils.isEmpty(reason)) {
            return "";
        } else if (SysVariable.AUDIT_REFUSE_TYPE_AUTO.equals(type)) {
            return "您授信资格不符合要求";
        }
        String[] s = reason.split(":");
        if (s.length >= 2) {
            if (s[1].contains("名单")) {
                return "不符合授信要求";
            } else {
                return s[1];
            }
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println("非白名单用户".contains("名单"));
    }

}
