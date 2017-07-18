package com.github.xabgesagtx.mensa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for the label cache
 */
@Configuration
@ConfigurationProperties(prefix = "cache.labels")
@Getter
@Setter
public class LabelCacheConfig {
    private List<String> stopwords;
}
