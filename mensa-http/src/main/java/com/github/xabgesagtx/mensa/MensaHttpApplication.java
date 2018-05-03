package com.github.xabgesagtx.mensa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for the http application
 */
@SpringBootApplication
@EnableScheduling
public class MensaHttpApplication {

	public static void main(String[] args) {
		SpringApplication.run(MensaHttpApplication.class, args);
	}

}
