package com.github.xabgesagtx.mensa.jobs;

import com.github.xabgesagtx.mensa.common.MensaConstants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Creates a zip file from all exported csv files and copies it to the destination directory
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class ZipAndCopyJob implements Runnable {

	@Setter
	private Path exportDirPath;

	@Setter
	private Path destinationDirPath;

	@Override
	public void run() {
		log.info("Starting zip and copy job");
		String zipFileName = getZipFileName();
		Path zipPath = Paths.get(exportDirPath.toAbsolutePath().toString(), zipFileName);
		boolean created = false;
		try {
			 Files.createFile(zipPath);
			 created = true;
		} catch (IOException e) {
			log.error("Failed to create zip file {} due to error: {}", zipPath, e.getMessage());
		}
		if (created && copyToZip(zipPath)) {
			try {
				Path destinationZipPath = destinationDirPath.resolve(zipFileName);
				Files.move(zipPath, destinationZipPath);
				log.info("Copied zip archive to destination directory {}", destinationZipPath);
			} catch (IOException e) {
				log.error("Failed to copy zipFile {} to destination directory");
			}
		}
	}

	private boolean copyToZip(Path zipPath) {
		boolean success = false;
		try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zipPath))) {
			Files.walk(exportDirPath, 1)
					.filter(path -> path.toString().endsWith(".csv"))
					.forEach(path -> {
						log.info("Adding file {} to zip archive", path);
						ZipEntry zipEntry = new ZipEntry(path.getFileName().toString());
						try {
							out.putNextEntry(zipEntry);
							Files.copy(path, out);
							out.closeEntry();
						} catch (IOException e) {
							log.error("Failed to add entry {} due to error: {}", path, e.getMessage());
						}
					});
			success = true;
		} catch (IOException e) {
			log.error("Failed to get files for zip file due to error: {}", e.getMessage());
		}
		return success;
	}

	private String getZipFileName() {
		return MensaConstants.EXPORT_DATE_FORMATTER.format(LocalDateTime.now()) + ".zip";
	}
}
