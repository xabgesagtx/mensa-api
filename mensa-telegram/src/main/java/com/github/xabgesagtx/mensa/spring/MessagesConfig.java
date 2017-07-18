package com.github.xabgesagtx.mensa.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.concurrent.TimeUnit;

/**
 * Configruation class for supporting messages and locales
 */
public class MessagesConfig {

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setCacheSeconds(Long.valueOf(TimeUnit.SECONDS.convert(1, TimeUnit.HOURS)).intValue());
        return messageSource;
    }

}
