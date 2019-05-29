package com.ivay.ivay_app.config;

import com.ivay.ivay_common.utils.LocaleUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18nService {

    private final MessageSource messageSource;

    private Locale locale;

    public I18nService(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.locale = LocaleUtils.getContextLocale();
    }

    public String getMessage(String msgKey, Object[] args) {
        return messageSource.getMessage(msgKey, args, locale);
    }

    public String getMessage(String msgKey) {
        return messageSource.getMessage(msgKey, null, locale);
    }

    public void setLang(String lang) {
        this.locale = LocaleUtils.getLocale(lang);
    }

    public Locale getLang() {
        return locale;
    }
}
