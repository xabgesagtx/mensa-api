package com.github.xabgesagtx.mensa.jobs;

import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Export mensas as csv
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class ExportMensasJob implements Runnable {

	private final Path exportDirPath;

	@Autowired
	private MensaRepository repo;

	@Override
	public void run() {
		log.info("Start exporting mensas");
		List<Mensa> mensas = repo.findAllByOrderByName();
		File outputFile = exportDirPath.resolve("mensas.csv").toFile();
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("id", "mainUrl", "name", "nextWeekUrl", "thisWeekUrl", "todayUrl", "tomorrowUrl").withRecordSeparator('\n');
		try (FileWriter writer = new FileWriter(outputFile);
			 CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)
		) {
			for (Mensa mensa : mensas) {
				csvPrinter.printRecord(mensa.getId(), mensa.getMainUrl(), mensa.getName(), mensa.getNextWeekUrl(), mensa.getThisWeekUrl(), mensa.getTodayUrl(), mensa.getTomorrowUrl());
			}
			log.info("Finished exporting {} mensas to file {}", mensas.size(), outputFile);
		} catch (IOException e) {
			log.error("Failed to export mensas to file {} due to error: {}", outputFile.getAbsolutePath(), e.getMessage());
		}

	}
}
