package com.github.xabgesagtx.mensa.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Main spring configuration class
 */
@Configuration
public class SpringConfig {

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor result = new ThreadPoolTaskExecutor();
		result.setMaxPoolSize(1);
		result.setCorePoolSize(1);
		return result;
	}
}
