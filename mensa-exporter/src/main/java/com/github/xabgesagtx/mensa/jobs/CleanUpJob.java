package com.github.xabgesagtx.mensa.jobs;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Removes temporary files and folders
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class CleanUpJob implements Runnable {

	@Setter
	private Path exportDirPath;

	@Override
	public void run() {
		log.info("Starting clean up job");
		try {
			log.info("Deleting directory {}", exportDirPath);
			FileUtils.deleteDirectory(exportDirPath.toFile());
			log.info("Finished clean up job");
		} catch (IOException e) {
			log.error("Failed to cleanup export directory {} due to error: {}", exportDirPath, e.getMessage());
		}
	}
}
