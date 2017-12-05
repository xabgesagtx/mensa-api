package com.github.xabgesagtx.mensa.jobs;

import com.github.xabgesagtx.mensa.config.ExporterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Runs periodic export jobs of mensa database
 */
@Component
@Slf4j
public class JobScheduler {

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private ExporterConfig config;

	@Scheduled(cron = "${exporter.cron}")
	public void runExportJobs() {
		log.info("Starting export jobs");
		Path destinationPath = Paths.get(config.getDestinationDir());
		if (!Files.exists(destinationPath)) {
			log.error("Not exporting anything because destination path {} does not exist", destinationPath);
		} else if (!Files.isDirectory(destinationPath)) {
			log.error("Not exporting anything because destination path {} is not a directory", destinationPath);
		} else {
			createTempPath().ifPresent(tempPath -> {
				startMensaExportJob(tempPath);
				startDishesExportJob(tempPath);
				startAllergensExportJob(tempPath);
				startLabelsExportJob(tempPath);
				startZipAndCopyJob(tempPath, destinationPath);
				startCleanUpJob(tempPath);
			});
		}
	}

	private void startLabelsExportJob(Path tempPath) {
		ExportLabelsJob job = beanFactory.getBean(ExportLabelsJob.class, tempPath);
		taskExecutor.execute(job);
	}

	private void startAllergensExportJob(Path tempPath) {
		ExportAllergensJob job = beanFactory.getBean(ExportAllergensJob.class, tempPath);
		taskExecutor.execute(job);
	}

	private void startDishesExportJob(Path tempPath) {
		ExportDishesJob job = beanFactory.getBean(ExportDishesJob.class, tempPath);
		taskExecutor.execute(job);
	}

	@PostConstruct
	public void exportOnStartup() {
		if (config.isExportOnStartup()) {
			runExportJobs();
		}
	}

	private void startZipAndCopyJob(Path tempPath, Path destinationPath) {
		ZipAndCopyJob job = beanFactory.getBean(ZipAndCopyJob.class);
		job.setExportDirPath(tempPath);
		job.setDestinationDirPath(destinationPath);
		taskExecutor.execute(job);
	}

	private void startCleanUpJob(Path tempPath) {
		CleanUpJob job = beanFactory.getBean(CleanUpJob.class);
		job.setExportDirPath(tempPath);
		taskExecutor.execute(job);
	}

	private void startMensaExportJob(Path tempPath) {
		ExportMensasJob expo = beanFactory.getBean(ExportMensasJob.class, tempPath);
		taskExecutor.execute(expo);
	}

	private Optional<Path> createTempPath() {
		Optional<Path> result = Optional.empty();
		Path tempPath = Paths.get(config.getTempDir(), Long.toString(System.currentTimeMillis()));
		if (Files.exists(tempPath)) {
			log.error("Cannot create temp directory {} because it already exists", tempPath);
		} else {
			try {
				result = Optional.of(Files.createDirectory(tempPath));
			} catch (IOException e) {
				log.error("Failed to create directory {} due to error: {}", tempPath, e.getMessage());
			}
		}
		return result;
	}
}
