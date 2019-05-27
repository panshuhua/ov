package com.ivay.ivay_app.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class LocaleUtils {
    private static final Logger logger = LoggerFactory.getLogger(LocaleUtils.class);

    private LocaleUtils() {
    }

    public static Locale getContextLocale() {
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
}