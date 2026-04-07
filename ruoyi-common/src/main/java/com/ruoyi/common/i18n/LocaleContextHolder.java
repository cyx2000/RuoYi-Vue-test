package com.ruoyi.common.i18n;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContext;

public class LocaleContextHolder {

    private final static ScopedValue<LocaleContext> LOCALE_CONTEXT = ScopedValue.newInstance();

    public static ScopedValue<LocaleContext> getKey()
    {
        return LOCALE_CONTEXT;
    }

    public static Locale getLocale() {
        return LOCALE_CONTEXT.get().getLocale();
    }

}
