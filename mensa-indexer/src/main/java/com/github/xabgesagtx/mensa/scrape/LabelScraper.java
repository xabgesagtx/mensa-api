package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.config.ScrapeConfig;
import com.github.xabgesagtx.mensa.model.Label;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scraper to scrape dish labels from a website
 */
@Component
public class LabelScraper extends AbstractSelfContainedScraper<List<Label>> {

    @Autowired
    private ScrapeConfig config;

    @Override
    public List<Label> scrape() {
        return scrape(config.getAllergenAndCategoryUrl());
    }

    @Override
    List<Label> scrapeFromDocument(Document document) {
        return document.select(".content_list  div strong:has(img)").stream().flatMap(this::getCategoryFromElem).collect(Collectors.toList());
    }

    private Stream<Label> getCategoryFromElem(Element elem) {
        Stream<Label> result = Stream.empty();
        Optional<String> img = elem.select("img").stream().map(e -> e.absUrl("src")).filter(StringUtils::isNotBlank).findFirst();
        String text = elem.text();
        if (img.isPresent() && StringUtils.isNotBlank(text)) {
            result = Stream.of(Label.of(text, img.get()));
        }
        return result;
    }

    @Override
    List<Label> getDefault() {
        return Collections.emptyList();
    }
}
