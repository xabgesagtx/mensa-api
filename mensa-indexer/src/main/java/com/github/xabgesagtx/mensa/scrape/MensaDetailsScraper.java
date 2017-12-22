package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.scrape.model.MensaDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scrapes mensa details as the address, zipcode or the name of the city
 */
@Component
@Slf4j
public class MensaDetailsScraper extends AbstractSimpleScraper<Optional<MensaDetails>> {

	private static final Pattern ZIPCODE_PATTERN = Pattern.compile("(\\d\\d\\d\\d\\d)\\s+([^\\s]+)");

	@AllArgsConstructor
	private class CityDetails {
		private String zipcode;
		private String city;
	}

	@Override
	Optional<MensaDetails> scrapeFromDocument(Document document) {
		return document.select("#cafeteria p").stream().flatMap(this::getFromCafeteriaElem).findFirst();
	}

	@Override
	Optional<MensaDetails> getDefault() {
		return Optional.empty();
	}

	private Stream<MensaDetails> getFromCafeteriaElem(Element elem) {
		Stream<MensaDetails> result = Stream.empty();
		elem.select("br").append("\\n");
		List<String> lines = Stream.of(StringUtils.split(elem.text().replaceAll("\\\\n", "\n"), "\n"))
				.flatMap(line -> Stream.of(StringUtils.split(line, ",")))
				.map(StringUtils::trim)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
		if (lines.size() >= 2) {
			String address = lines.get(0);
			result = getCityInfo(lines.get(1)).map(details -> MensaDetails.builder().address(address).city(details.city).zipcode(details.zipcode).build());
		}
		return result;
	}

	private Stream<CityDetails> getCityInfo(String line) {
		Stream<CityDetails> result = Stream.empty();
		Matcher matcher = ZIPCODE_PATTERN.matcher(line);
		if (matcher.matches()) {
			result = Stream.of(new CityDetails(matcher.group(1), matcher.group(2)));
		}
		return result;
	}
}
