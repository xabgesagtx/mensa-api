package com.github.xabgesagtx.mensa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("exports")
@Data
public class ExportsConfig {

	private String exportsDir;

}
