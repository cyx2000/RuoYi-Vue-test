package com.ruoyi.framework.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.ScopedValue;

/**
 * 数据源切换处理
 *
 * @author ruoyi
 */
public class DynamicDataSourceContextHolder
{
    public static final Logger log = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    /**
     * 使用ScopedValue维护变量，ScopedValue为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地设置自己的副本，设置后无法更改，不会影响其它线程所对应的副本。
     */
    private static final ScopedValue<String> CONTEXT_HOLDER = ScopedValue.newInstance();

    /**
     * 获得数据源的变量
     */
    public static String getDataSourceType()
    {
        if(CONTEXT_HOLDER.isBound()) {
            return CONTEXT_HOLDER.get();
        }
        return "";
    }

    /**
     * 获得ScopedValue key
     */
    public static ScopedValue<String> getKey()
    {
        return CONTEXT_HOLDER;
    }
}
