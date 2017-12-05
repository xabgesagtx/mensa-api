package com.github.xabgesagtx.mensa.jobs;

import com.github.xabgesagtx.mensa.model.Allergen;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.AllergenRepository;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import lombok.AllArgsConstructor;
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
 * Export allergens as csv
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class ExportAllergensJob implements Runnable {

	private final Path exportDirPath;

	@Autowired
	private AllergenRepository repo;

	@Override
	public void run() {
		log.info("Start exporting allergens");
		List<Allergen> allergens = repo.findAllByOrderByNumberAsc();
		File outputFile = exportDirPath.resolve("allergens.csv").toFile();
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("number", "name").withRecordSeparator('\n');
		try (FileWriter writer = new FileWriter(outputFile);
			 CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)
		) {
			for (Allergen allergen : allergens) {
				csvPrinter.printRecord(allergen.getNumber(), allergen.getName());
			}
			log.info("Finished exporting {} allergens to file {}", allergens.size(), outputFile);
		} catch (IOException e) {
			log.error("Failed to export allergens to file {} due to error: {}", outputFile.getAbsolutePath(), e.getMessage());
		}

	}
}
