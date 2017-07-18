package com.github.xabgesagtx.mensa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Main configuration class for the bot
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfig {

    private String botname;
    private String token;
    private List<FilterConfig> filters;

}
