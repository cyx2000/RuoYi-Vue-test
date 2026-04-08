package com.ruoyi.i18n.util;

import java.text.MessageFormat;
import java.util.Locale;

import org.jspecify.annotations.Nullable;
import org.springframework.context.support.AbstractMessageSource;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.i18n.LocaleContextHolder;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.i18n.domain.LangTrans;
import com.ruoyi.i18n.service.ILangTransService;

import jakarta.annotation.Resource;

public class MessageResource extends AbstractMessageSource {

    @Resource
    RedisCache redisCache;

    @Resource
    ILangTransService langTransService;

    protected final String SPLIT_CODE = ":";

    private String getText(String code, Locale locale) {
        String lang = locale.getLanguage();

        int lastDot = StringUtils.lastIndexOf(code, '.');

        String moduleKey = StringUtils.substring(code, 0, lastDot); // eg: user.login

        String label = StringUtils.substring(code, lastDot + 1);; // eg: notExist

        String key = lang + SPLIT_CODE + moduleKey; // eg: zh:user.login

        String resourceText = code;

        LangTrans transtext = redisCache.getCacheMapValue(key, label);

        String localeText = null;

        if (StringUtils.isNotNull(transtext))
        {
            localeText = transtext.getTransText();
        }

        if(StringUtils.isEmpty(localeText))
        {
            langTransService.tryLoadModuleTransTextToRedis(moduleKey);

            transtext = redisCache.getCacheMapValue(key, label);

            localeText = transtext.getTransText();
        }

        if (StringUtils.isNotEmpty(localeText)) {
            resourceText = localeText;
        }
        return resourceText;
    }

    @Override
    protected @Nullable MessageFormat resolveCode(String code, Locale locale) {
        Locale target = LocaleContextHolder.getLocale();
        String msg = getText(code, target);
        MessageFormat result = createMessageFormat(msg, target);
        return result;
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        Locale target = LocaleContextHolder.getLocale();
        String result = getText(code, target);
        return result;
    }

}
