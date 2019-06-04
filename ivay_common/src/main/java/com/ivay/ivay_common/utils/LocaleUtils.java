package com.ivay.ivay_common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class LocaleUtils {
    private static final Logger logger = LoggerFactory.getLogger(LocaleUtils.class);

    private LocaleUtils() {
    }

    public static Locale getContextLocale() {
        // 获取语言值，可根据请求头信息改变
        return LocaleContextHolder.getLocale();
    }
}