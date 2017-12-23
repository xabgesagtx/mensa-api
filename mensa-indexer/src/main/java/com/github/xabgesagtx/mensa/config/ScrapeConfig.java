package com.github.xabgesagtx.mensa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Main config file to set urls to scrape
 */
@Configuration
@ConfigurationProperties(prefix = "scrape")
@Setter
@Getter
public class ScrapeConfig {

    String mensasUrl;
    String allergenAndCategoryUrl;
    String nominatinUrl;
    Map<String,GeoPointConfig> coordinatesForMensaId = new HashMap<>();

}
