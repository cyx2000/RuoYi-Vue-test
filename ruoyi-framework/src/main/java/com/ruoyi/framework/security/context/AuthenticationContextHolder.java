package com.ruoyi.framework.security.context;

import org.springframework.security.core.Authentication;
import java.lang.ScopedValue;

/**
 * 身份验证信息
 *
 * @author ruoyi
 */
public class AuthenticationContextHolder
{
    private static final ScopedValue<Authentication> contextHolder = ScopedValue.newInstance();

    public static Authentication getContext()
    {
        return contextHolder.get();
    }

    public static ScopedValue<Authentication> getKey()
    {
        return contextHolder;
    }
}
