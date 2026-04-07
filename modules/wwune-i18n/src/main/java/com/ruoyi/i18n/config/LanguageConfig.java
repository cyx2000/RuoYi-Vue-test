package com.ruoyi.i18n.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ruoyi.i18n.util.MessageResource;

@Configuration
public class LanguageConfig {

    @Bean
    public MessageSource messageResource()
    {
        return new MessageResource();
    }
}
