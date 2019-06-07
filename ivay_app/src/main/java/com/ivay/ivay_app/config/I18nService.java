package com.ivay.ivay_app.config;

import com.ivay.ivay_common.utils.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class I18nService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    private final MessageSource messageSource;

    public I18nService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String msgKey, Object[] args) {
        logger.info("语言1：" + LocaleUtils.getContextLocale());
        return messageSource.getMessage(msgKey, args, LocaleUtils.getContextLocale());
    }

    public String getMessage(String msgKey) {
        logger.info("语言2：" + LocaleUtils.getContextLocale());
        return messageSource.getMessage(msgKey, null, LocaleUtils.getContextLocale());
    }
}
