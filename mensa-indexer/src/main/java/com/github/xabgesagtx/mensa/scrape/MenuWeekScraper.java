package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.model.Dish;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scraper to scrape dishes from a site that holds the menu for a week
 */
@Component
@Slf4j
public class MenuWeekScraper extends AbstractDishScraper {

	private static final Pattern BASE_DATE_PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}).+\\d{2}\\.\\d{2}\\.\\d{4}");
	private static final DateTimeFormatter BASE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Override
	Stream<LocalDate> getBaseDate(Element element) {
		return getBaseDateFromString(StringUtils.defaultString(element.text())).map(Stream::of).orElseGet(Stream::empty);
	}

	@VisibleForTesting
	Optional<LocalDate> getBaseDateFromString(String text) {
		Optional<LocalDate> result = Optional.empty();
		Matcher matcher = BASE_DATE_PATTERN.matcher(text);
		if (matcher.find()) {
			String dateAsString = matcher.group(1);
			try {
				result = Optional.of(LocalDate.parse(dateAsString, BASE_DATE_FORMATTER));
			} catch (DateTimeParseException e) {
				log.warn("Failed to parse base date from string: {}", text);
			}
		} else {
			log.warn("Failed to read base date from string: {}", text);
		}
		return result;
	}
	
	private Stream<Dish> getDishesFromRow(Element row, int rowIndex, LocalDate baseDate, String mensaId) {
		Elements categoryCell = row.select("th");
		Elements dishCells = row.select("td");
		if (!categoryCell.isEmpty() && dishCells.size() == 5) {
			String category = StringUtils.defaultString(categoryCell.get(0).text());
			AtomicInteger dayIndex = new AtomicInteger(0);
			return dishCells.stream().flatMap(cell -> getDishesFromCell(cell, category, rowIndex, baseDate.plusDays(dayIndex.getAndIncrement()), mensaId));
		}
		return Stream.empty();
	}


	private Stream<Dish> getDishesFromCell(Element cell, String category, int rowIndex, LocalDate date, String mensaId) {
		AtomicInteger paragraphIndex = new AtomicInteger(1);
		return cell.select("p").stream().flatMap(p -> {
			Stream<Dish> result = Stream.empty();
			List<String> labels = getLabels(p);
			Optional<String> description = p.select("strong").stream().findFirst().map(Element::text).filter(StringUtils::isNotBlank);
			List<String> allergens = getAllergens(p);
			List<BigDecimal> prices = p.select("span.price").stream().map(Element::text).filter(StringUtils::isNotBlank).map(this::getPricesFromString).findFirst().orElseGet(Lists::newArrayList);
			if (description.isPresent() && !prices.isEmpty()) {
				String id = getId(mensaId, date, rowIndex, paragraphIndex.getAndIncrement());
				result = Stream.of(Dish.of(id, date, mensaId, category, description.get(), labels, prices, allergens));
			}
			return result;
		});

	}

	@Override
	List<Dish> scrapeFromTable(LocalDate baseDate, Elements rows, String mensaId) {
		AtomicInteger rowIndex = new AtomicInteger(1);
		return rows
				.stream()
				.flatMap(row -> getDishesFromRow(row, rowIndex.getAndIncrement(), baseDate, mensaId))
				.collect(Collectors.toList());
	}

	@Override
	String getTableSelector() {
		return "table#week-menu";
	}
}
