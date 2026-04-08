package com.ruoyi.i18n.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.i18n.LanguageSource;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.i18n.domain.LangLanguage;
import com.ruoyi.i18n.service.ILangLanguageService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class LanguageSourceImpl implements LanguageSource {

    @Resource
    private RedisCache redisCache;

    @Resource
    private ILangLanguageService languageService;

    protected final String DEFAULT_LOCALE_KEY = "i18nlanguage:defaultLanguage";

    protected final String LOCALES_KEY = "i18nlanguage:languageList";

    @PostConstruct
    public void loadAllLanguagesToRedis()
    {
        List<LangLanguage> allLanguages = languageService.selectLangLanguageList(null);

        List<String> langList = new ArrayList<>(allLanguages.size());

        for (LangLanguage lang : allLanguages) {
            langList.add(lang.getLangTag());
            if (StringUtils.equals(lang.getIsDefault(), "Y"))
            {
                redisCache.setCacheObject(DEFAULT_LOCALE_KEY, lang.getLangTag(), 999, TimeUnit.DAYS);
            }
        }

        redisCache.setCacheList(LOCALES_KEY, langList);
        redisCache.expire(LOCALES_KEY, 999, TimeUnit.DAYS);
    }

    @Override
    public Locale getLocale(HttpServletRequest request) {
        String lang = request.getHeader("lang");

        Locale newLocale = null;

        if (StringUtils.isNotEmpty(lang))
        {
            List<String> allLanguages = redisCache.getCacheList(LOCALES_KEY);

            if (allLanguages.contains(lang))
            {
                newLocale = Locale.of(lang);
            }
        } else {
            String defaultLanguage = redisCache.getCacheObject(DEFAULT_LOCALE_KEY);

            newLocale = Locale.of(defaultLanguage);
        }
        return newLocale;
    }

}
