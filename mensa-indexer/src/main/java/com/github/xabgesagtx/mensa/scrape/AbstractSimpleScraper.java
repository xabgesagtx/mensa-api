package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.utils.WebUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Simple scraper base class
 * @param <T> type of the scraped content
 */
@Component
public abstract class AbstractSimpleScraper<T> {

    @Autowired
    private WebUtils utils;

    /**
     * Scrape content from an url
     * @param url to scrape content from
     * @return the scraped content
     */
    public T scrape(String url) {
        Optional<String> baseUrl = utils.getBaseUrl(url);
        return baseUrl.map(s -> utils.getDocumentAsString(url).map(documentAsString -> scrapeFromString(documentAsString, s)).orElseGet(this::getDefault)).orElseGet(this::getDefault);
    }

    protected T scrapeFromString(String documentAsString, String baseUrl) {
        Document document = Jsoup.parse(documentAsString, baseUrl);
        return scrapeFromDocument(document);
    }

    /**
     * Scrape content from a jsoup document
     * @param document to scrape from
     * @return the scraped content
     */
    abstract T scrapeFromDocument(Document document);

    /**
     * Default if no content could be scraped
     * @return default value
     */
    abstract T getDefault();
}
