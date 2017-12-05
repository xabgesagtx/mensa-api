package com.github.xabgesagtx.mensa.exports;

import com.github.xabgesagtx.mensa.common.MensaConstants;
import com.github.xabgesagtx.mensa.config.ExportsConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class ExportsManager {

	@Autowired
	private ExportsConfig config;

	public List<ExportDTO> getExports() {
		List<ExportDTO> result = Collections.emptyList();
		if (StringUtils.isBlank(config.getExportsDir())) {
			log.error("No exports directory configured");
		} else {
			File exportsDir = new File(config.getExportsDir());
			if (exportsDir.isFile()) {
				log.error("Exports directory {} is a file", config.getExportsDir());
			} else if (!exportsDir.exists()) {
				log.error("Exports directory {} does not exist", config.getExportsDir());
			} else {
				try {
					result = Files.walk(exportsDir.toPath(), 1)
							.filter(path -> path.toString().endsWith(".zip"))
							.map(Path::toFile)
							.flatMap(this::toExportDTO)
							.sorted()
							.collect(Collectors.toList());
				} catch (IOException e) {
					log.error("Failed to ");
				}
			}
		}
		return result;
	}

	public Optional<File> getExportAsFile(LocalDateTime dateTime) {
		Optional<File> result = Optional.empty();
		if (StringUtils.isBlank(config.getExportsDir())) {
			log.error("Could not return exports file because exports directory is not configured");
		} else {
			String filename = MensaConstants.EXPORT_DATE_FORMATTER.format(dateTime) + ".zip";
			Path path = Paths.get(config.getExportsDir(), filename);
			File file = path.toFile();
			if (file.isFile()) {
				result = Optional.of(file);
			} else {
				log.info("Could not find exports file {}", filename);
			}
		}
		return result;
	}

	private Stream<ExportDTO> toExportDTO(File file) {
		Stream<ExportDTO> result = Stream.empty();
		long length = file.length();
		String name = file.getName();
		String dateString = StringUtils.removeEnd(name,".zip");
		if (StringUtils.isNotBlank(dateString)) {
			try {
				LocalDateTime dateTime = LocalDateTime.parse(dateString, MensaConstants.EXPORT_DATE_FORMATTER);
				result = Stream.of(new ExportDTO(dateTime, length));
			} catch (DateTimeParseException e) {
				log.debug("Ignoring file {} due to parse error: {}", name, e.getMessage());
			}
		} else {
			log.debug("Ignoring file {}", name);
		}
		return result;
	}
}
