package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.config.ScrapeConfig;
import com.github.xabgesagtx.mensa.model.Mensa;
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
public class MensaScraper extends AbstractSelfContainedScraper<List<Mensa>> {
	
	@Autowired
	private MenuUrlScraper urlScraper;

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
				if (mensaIdOpt.isPresent() && menuUrlsOpt.isPresent()) {
					String mensaId = mensaIdOpt.get();
					MenuUrls menuUrls = menuUrlsOpt.get();
					result = Stream.of(Mensa.builder().name(name).id(mensaId).mainUrl(mensaUrl).todayUrl(menuUrls.getToday()).thisWeekUrl(menuUrls.getThisWeek()).nextWeekUrl(menuUrls.getNextWeek()).tomorrowUrl(menuUrls.getTomorrow()).updatedAt(LocalDateTime.now()).build());
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
