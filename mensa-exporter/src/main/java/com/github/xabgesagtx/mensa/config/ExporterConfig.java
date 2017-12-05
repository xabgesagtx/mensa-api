package com.github.xabgesagtx.mensa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of exporter
 */
@Data
@Configuration
@ConfigurationProperties("exporter")
public class ExporterConfig {

	private String tempDir;
	private String destinationDir;
	private boolean exportOnStartup;

}
