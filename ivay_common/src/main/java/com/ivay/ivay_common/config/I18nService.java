package com.ivay.ivay_common.config;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.ivay.ivay_common.utils.LocaleUtils;

@Component
public class I18nService {
    private final MessageSource messageSource;

    public I18nService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String msgKey, Object[] args) {
        return messageSource.getMessage(msgKey, args, LocaleUtils.getContextLocale());
    }

    public String getMessage(String msgKey) {
        return messageSource.getMessage(msgKey, null, LocaleUtils.getContextLocale());
    }

    /**
     * 获取越南语提示语
     * 
     * @param msgKey
     * @return
     */
    public String getViMessage(String msgKey) {
        return messageSource.getMessage(msgKey, null, LocaleUtils.getViLocale());
    }

}
