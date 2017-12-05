package com.github.xabgesagtx.mensa.jobs;

import com.github.xabgesagtx.mensa.model.Allergen;
import com.github.xabgesagtx.mensa.model.Label;
import com.github.xabgesagtx.mensa.persistence.AllergenRepository;
import com.github.xabgesagtx.mensa.persistence.LabelRepository;
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
import java.util.List;

/**
 * Export labels as csv
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class ExportLabelsJob implements Runnable {

	private final Path exportDirPath;

	@Autowired
	private LabelRepository repo;

	@Override
	public void run() {
		log.info("Start exporting labels");
		List<Label> labels = repo.findAllByOrderByName();
		File outputFile = exportDirPath.resolve("labels.csv").toFile();
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("name", "imageUrl").withRecordSeparator('\n');
		try (FileWriter writer = new FileWriter(outputFile);
			 CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)
		) {
			for (Label label : labels) {
				csvPrinter.printRecord(label.getName(), label.getImageUrl());
			}
			log.info("Finished exporting {} labels to file {}", labels.size(), outputFile);
		} catch (IOException e) {
			log.error("Failed to export labels to file {} due to error: {}", outputFile.getAbsolutePath(), e.getMessage());
		}

	}
}
