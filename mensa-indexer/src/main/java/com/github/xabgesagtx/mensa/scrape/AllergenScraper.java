package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.config.ScrapeConfig;
import com.github.xabgesagtx.mensa.model.Allergen;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scraper to scrape allergens from a website
 */
@Component
public class AllergenScraper extends AbstractSelfContainedScraper<List<Allergen>> {

    private static final Pattern PATTERN = Pattern.compile("br\\\\?>\\s*(\\d+)\\s*([^>]+)\\s*<");

    @Autowired
    private ScrapeConfig config;

    @Override
    protected List<Allergen> scrapeFromDocument(Document document) {
        return document.select(".content_list").stream().map(Element::html).flatMap(this::getAllergensFromHtml).collect(Collectors.toList());
    }

    private Stream<Allergen> getAllergensFromHtml(String html) {
        Stream<Allergen> result = Stream.empty();
        Matcher matcher = PATTERN.matcher(html);
        while (matcher.find()) {
            String number = matcher.group(1);
            String name = matcher.group(2);
            if (StringUtils.isNotBlank(number) && StringUtils.isNotBlank(name) && StringUtils.isNumeric(number)) {
                result = Stream.concat(result,Stream.of(Allergen.of(Integer.parseInt(number),name.trim())));
            }
        }
        return result;
    }

    @Override
    List<Allergen> getDefault() {
        return Collections.emptyList();
    }


    @Override
    public List<Allergen> scrape() {
        return scrape(config.getAllergenAndCategoryUrl());
    }
}
