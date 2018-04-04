package com.github.xabgesagtx.mensa.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

	@Bean
	public ConfigurableServletWebServerFactory webServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
		mappings.add("graphqls","application/graphql");
		factory.setMimeMappings(mappings);
		return factory;
	}
}
