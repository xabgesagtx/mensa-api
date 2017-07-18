package com.github.xabgesagtx.mensa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the indexer
 */
@SpringBootApplication
@EnableScheduling
public class MensaIndexerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MensaIndexerApplication.class, args);
	}

}
