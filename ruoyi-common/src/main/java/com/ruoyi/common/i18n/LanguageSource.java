package com.ruoyi.common.i18n;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

public interface LanguageSource {

    public Locale getLocale(HttpServletRequest request);
}
