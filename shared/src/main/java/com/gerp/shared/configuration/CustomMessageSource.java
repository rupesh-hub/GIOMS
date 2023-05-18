package com.gerp.shared.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CustomMessageSource {

    private final MessageSource messageSource;

    @Autowired
    public CustomMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String code) {
        return messageSource.getMessage(code, null, getCurrentLocale());
    }

    public String get(String code, Object... objects) {
        return messageSource.getMessage(code, objects, getCurrentLocale());
    }

    public Locale getCurrentLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        if (locale.getDisplayLanguage().equalsIgnoreCase("np")) {
            locale = new Locale("np", "Nepal");
        }
        return locale;
    }

    public String getEnglish(String code, Object... objects) {
        return messageSource.getMessage(code, objects, LocaleContextHolder.getLocale());
    }

    public String getNepali(String code, Object... objects) {
        return messageSource.getMessage(code, objects, new Locale("np", "Nepal"));
    }
}
