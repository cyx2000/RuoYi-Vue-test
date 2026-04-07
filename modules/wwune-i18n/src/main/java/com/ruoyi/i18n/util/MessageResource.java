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
        String localeCode = locale.getLanguage();
        String key = localeCode + SPLIT_CODE + code; // eg: zh:user.login.notExist

        String resourceText = code;

        LangTrans transtext = redisCache.getCacheObject(key);

        String localeText = null;

        if (StringUtils.isNotNull(transtext))
        {
            localeText = transtext.getTransText();
        }

        if(StringUtils.isEmpty(localeText))
        {
            langTransService.tryLoadModuleTransTextToRedis(getModuleKey(code));

            transtext = redisCache.getCacheObject(key);

            localeText = transtext.getTransText();
        }

        if (StringUtils.isNotEmpty(localeText)) {
            resourceText = localeText;
        }
        return resourceText;
    }

    protected String getModuleKey(String input)
    {
        String moduleKey = String.copyValueOf(input.toCharArray());
        int lastDot = StringUtils.lastIndexOf(input, '.');
        return StringUtils.substring(moduleKey, 0, lastDot);
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
