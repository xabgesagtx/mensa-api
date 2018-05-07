package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.config.ScrapeConfig;
import com.github.xabgesagtx.mensa.model.Allergen;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
    private static final Pattern PATTERN_CHARACTERS = Pattern.compile("p>\\s*(\\w+)\\s*([^>]+)\\s*<");

    @Autowired
    private ScrapeConfig config;

	private final IntegerAwareStringComparator comparator = new IntegerAwareStringComparator();

    @Override
    protected List<Allergen> scrapeFromDocument(Document document) {
        List<Allergen> result = document.select(".content_list").stream().map(Element::html).flatMap(this::getAllergensFromHtml).collect(Collectors.toList());

		result.addAll(document.select(".content_list").stream().map(Element::children).flatMap(this::getAllergensFromElements).sorted((a1,a2) -> comparator.compare(a1.getNumber(),a2.getNumber())).collect(Collectors.toList()));
        return result;
    }

    private Stream<Allergen> getAllergensFromHtml(String html) {
    	String cleanHtml = StringUtils.replaceAll(html, "&nbsp;", " ");
        Stream<Allergen> result = Stream.empty();
        Matcher matcher = PATTERN.matcher(cleanHtml);
        while (matcher.find()) {
            String number = matcher.group(1);
            String name = matcher.group(2);
            if (StringUtils.isNotBlank(number) && StringUtils.isNotBlank(name)) {
                result = Stream.concat(result,Stream.of(Allergen.of(number.trim(),name.trim())));
            }
        }
        return result;
    }

    private Stream<Allergen> getAllergensFromElements(Elements elements) {
    	Stream<Allergen> result = Stream.empty();
    	if (elements.size() >= 4) {
			Element subElement = elements.get(3);
			result = subElement.select("p").stream()
					.filter(elem -> !StringUtils.startsWithAny(elem.text(), "Wir", "In", "Lebensmittel", "Sie"))
					.map(Element::html)
					.map(text -> StringUtils.replaceAll(text, "(&nbsp;)|(<br>)", " "))
					.map(text -> StringUtils.replaceAll(text, "\\s+", " "))
					.map(StringUtils::trimToEmpty)
					.map(text -> text.split(" ", 2))
					.filter(array -> array.length > 1 && StringUtils.isNoneBlank(array))
					.map(array -> Allergen.of(array[0].trim(), array[1].trim()));
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
