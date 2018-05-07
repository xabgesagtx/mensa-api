package com.github.xabgesagtx.mensa.jobs;

import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.DishRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Export dishes as csv
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class ExportDishesJob implements Runnable {

	private static final String JOIN_SEPARATOR = "|";

	private final Path exportDirPath;

	@Autowired
	private MensaRepository mensaRepository;

	@Autowired
	private DishRepository dishRepository;

	@Override
	public void run() {
		log.info("Start exporting dishes");
		List<Mensa> mensas = mensaRepository.findAllByOrderByName();
		File outputFile = exportDirPath.resolve("dishes.csv").toFile();
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("id", "date", "mensaId", "category", "description", "labels", "prices","allergens").withRecordSeparator('\n');
		try (FileWriter writer = new FileWriter(outputFile);
			 CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat))
		{
			for (Mensa mensa : mensas) {
				exportMensa(mensa, csvPrinter);
			}
			log.info("Finished exporting dishes for {} mensas to file {}", mensas.size(), outputFile);
		} catch (IOException e) {
			log.error("Failed to export dishes to file {} due to error: {}", outputFile.getAbsolutePath(), e.getMessage());
		}
	}

	private void exportMensa(Mensa mensa, CSVPrinter csvPrinter) throws IOException {
		List<Dish> dishes = dishRepository.findByMensaId(mensa.getId());
		for (Dish dish : dishes) {
			String labelsString = dish.getLabels().stream().collect(Collectors.joining(JOIN_SEPARATOR));
			String pricesString = dish.getPrices().stream().map(price -> String.format(Locale.ENGLISH, "%.2f", price)).collect(Collectors.joining(JOIN_SEPARATOR));
			String allergensString = String.join(JOIN_SEPARATOR, dish.getAllergens());
			csvPrinter.printRecord(dish.getId(), DateTimeFormatter.ISO_DATE.format(dish.getDate()), dish.getMensaId(), dish.getCategory(), dish.getDescription(), labelsString, pricesString, allergensString);
		}
	}

}
