package com.github.xabgesagtx.mensa.scrape;

/**
 * Scraper that doesn't need additional information to scrape content
 * @param <T> type to be scraped
 */
public abstract class AbstractSelfContainedScraper<T> extends AbstractSimpleScraper<T> {

    /**
     * Scrape without additional information
     * @return the scraped content
     */
    public abstract T scrape();

}
