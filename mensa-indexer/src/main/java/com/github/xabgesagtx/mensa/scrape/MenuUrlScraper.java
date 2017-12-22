package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.scrape.model.MenuUrls;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Scrapes the relevant urls for the available menus for a mensa
 */
@Component
class MenuUrlScraper extends AbstractSimpleScraper<Optional<MenuUrls>> {

	@Override
	Optional<MenuUrls> scrapeFromDocument(Document document) {
		Optional<MenuUrls> result = Optional.empty();
		Elements elements = document.select("#content li");
		if (elements.size() == 4) {
			String today = getUrlFromElem(elements.get(0));
			String tomorrow = getUrlFromElem(elements.get(1));
			String thisWeek = getUrlFromElem(elements.get(2));
			String nextWeek = getUrlFromElem(elements.get(3));
			result = Optional.of(MenuUrls.of(today, tomorrow, thisWeek, nextWeek));
		} else if (elements.size() == 3) {
			String today = StringUtils.EMPTY;
			String tomorrow = getUrlFromElem(elements.get(0));
			String thisWeek = getUrlFromElem(elements.get(1));
			String nextWeek = getUrlFromElem(elements.get(2));
			result = Optional.of(MenuUrls.of(today, tomorrow, thisWeek, nextWeek));
		}
		return result;
	}

	@Override
	Optional<MenuUrls> getDefault() {
		return Optional.empty();
	}

	private String getUrlFromElem(Element elem) {
		return elem.select("a").stream().map(anchor -> anchor.absUrl("href")).map(StringUtils::trimToEmpty).findFirst().orElse(StringUtils.EMPTY);
	}



}
