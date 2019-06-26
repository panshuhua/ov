package com.ivay.ivay_common.utils;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

public class LocaleUtils {
    private static final Logger logger = LoggerFactory.getLogger(LocaleUtils.class);

    private LocaleUtils() {}

    public static Locale getContextLocale() {
        // 获取语言值，可根据请求头信息改变
        return LocaleContextHolder.getLocale();
    }

    public static Locale getLocale(String lang) {
        if (StringUtils.isEmpty(lang)) {
            return getContextLocale();
        }

        Locale locale;
        try {
            locale = new Locale(lang);
        } catch (Exception ex) {
            logger.info("创建语言对象报错，采用默认语言值");
            locale = getContextLocale();
        }
        return locale;
    }

    /**
     * 发送到期通知和短信时，直接使用越南语
     * 
     * @return
     */
    public static Locale getViLocale() {
        return getLocale(SysVariable.LANG_VI);
    }

}