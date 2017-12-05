package com.github.xabgesagtx.mensa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application to
 */
@SpringBootApplication
@EnableScheduling
public class MensaExporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(MensaExporterApplication.class, args);
	}

}
