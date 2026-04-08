package com.ruoyi.common.filter;

import java.io.IOException;
import java.util.Locale;

import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.i18n.LanguageSource;
import com.ruoyi.common.i18n.LocaleContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LanguageFilter extends OncePerRequestFilter {

    LanguageSource languageSource;

    public LanguageFilter(LanguageSource languageSource)
    {
        this.languageSource = languageSource;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        Locale lang = languageSource.getLocale(request);

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
