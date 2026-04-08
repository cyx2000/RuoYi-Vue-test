package com.ruoyi.framework.i18n;

import java.io.IOException;
import java.util.Locale;

import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.i18n.LanguageSource;
import com.ruoyi.common.i18n.LocaleContextHolder;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LanguageFilter extends OncePerRequestFilter {

    @Resource
    LanguageSource langSource;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        Locale lang = langSource.getLocale(request);

        ScopedValue.where(LocaleContextHolder.getKey(), new SimpleLocaleContext(lang)).run(
        new Runnable()
        {
            @Override
            public void run() {
                try {
                filterChain.doFilter(request, response);
                } catch (IOException e) {
                    throw new ServiceException(e.getMessage());
                } catch (ServletException e) {
                    throw new ServiceException(e.getMessage());
                }
            }
        });
    }

}
