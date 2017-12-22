package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.config.ScrapeConfig;
import com.github.xabgesagtx.mensa.geo.GeodataProvider;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.scrape.model.MensaDetails;
import com.github.xabgesagtx.mensa.scrape.model.MenuUrls;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scraper to scrape mensas from a website and it linked websites
 */
@Component
@Slf4j
public class MensaScraper extends AbstractSelfContainedScraper<List<Mensa>> {
	
	@Autowired
	private MenuUrlScraper urlScraper;

	@Autowired
	private MensaDetailsScraper detailsScraper;

	@Autowired
	private GeodataProvider geodataProvider;

	@Autowired
	private ScrapeConfig config;

	@Override
	List<Mensa> scrapeFromDocument(Document document) {
		return document.select("#content .introduction a").stream().flatMap(link -> {
			Stream<Mensa> result = Stream.empty();
			String mensaUrl = link.absUrl("href");
			String name = StringUtils.trim(link.text());
			if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(mensaUrl)) {
				Optional<String> mensaIdOpt = getIdFromMensaUrl(mensaUrl);
				Optional<MenuUrls> menuUrlsOpt = urlScraper.scrape(mensaUrl);
				if (!mensaIdOpt.isPresent()) {
					log.info("Could not get mensa id for mensa at url: \"{}\"", mensaUrl);
				} else if (!menuUrlsOpt.isPresent()) {
					log.info("Could not get mensa urls for mensa at url: \"{}\"", mensaUrl);
				} else {
					String mensaId = mensaIdOpt.get();
					MenuUrls menuUrls = menuUrlsOpt.get();
					Optional<MensaDetails> detailsOpt = detailsScraper.scrape(menuUrls.getToday());
					if (!detailsOpt.isPresent()) {
						log.info("Could not get mensa details for mensa at url: \"{}\"", mensaUrl);
					} else {
						MensaDetails details = detailsOpt.get();
						Mensa.MensaBuilder mensaBuilder = Mensa.builder()
								.name(name)
								.id(mensaId)
								.mainUrl(mensaUrl)
								.todayUrl(menuUrls.getToday())
								.thisWeekUrl(menuUrls.getThisWeek())
								.nextWeekUrl(menuUrls.getNextWeek())
								.tomorrowUrl(menuUrls.getTomorrow())
								.address(details.getAddress())
								.city(details.getCity())
								.zipcode(details.getZipcode())
								.updatedAt(LocalDateTime.now());
						geodataProvider.search(details.getAddress(), details.getZipcode(), details.getCity()).ifPresent(point -> mensaBuilder.point(point));
						result = Stream.of(mensaBuilder.build());
					}
				}
			}
			return result;
		}).collect(Collectors.toList());
	}

    @Override
    List<Mensa> getDefault() {
        return Collections.emptyList();
    }

    @Override
    public List<Mensa> scrape() {
        return scrape(config.getMensasUrl());
    }

	/**
	 * Parse the id from the url of a mensa
	 * @param mensaUrl url of the mensa
	 * @return id if available, empty otherwise
	 */
	public Optional<String> getIdFromMensaUrl(String mensaUrl) {
		Optional<String> result = Optional.empty();
		Pattern pattern = Pattern.compile("^.*/id/(\\d+)/?$");
		Matcher matcher = pattern.matcher(mensaUrl);
		if (matcher.matches()) {
			result = Optional.of(matcher.group(1));
		}
		return result;
	}
}
