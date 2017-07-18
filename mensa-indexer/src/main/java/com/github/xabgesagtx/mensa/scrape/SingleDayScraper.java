package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scraper to scrape a dishes from a website that holds the menu for a single day.
 *
 * This class can be used to parse dishes for days that aren't represented in the week menu. E.g. Saturday.
 */
@Component
@Slf4j
public class SingleDayScraper extends AbstractDishScraper {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy").withLocale(Locale.GERMANY);

    @Autowired
    private WebUtils utils;

    @Override
    List<Dish> scrapeFromTable(LocalDate baseDate, Elements rows, String mensaId) {
        List<Dish> result = new ArrayList<>();
        int rowIndex = 0;
        int paragraphIndex = 1;
        String previousCategory = StringUtils.EMPTY;
        for (Element row : rows) {
            String category = row.select("th").stream().map(Element::text).filter(StringUtils::isNotBlank).map(text -> text.replace("\u00a0",StringUtils.EMPTY)).findFirst().orElse(StringUtils.EMPTY);
            List<BigDecimal> prices = row.select("td.price").stream().flatMap(cell -> getPricesFromString(cell.text()).stream()).collect(Collectors.toList());
            Optional<Element> descriptionElemOpt = row.select("td.dish-description").stream().findFirst();
            if (descriptionElemOpt.isPresent()) {
                Element elem = descriptionElemOpt.get();
                String description = elem.text();
                List<String> labels = getLabels(elem);
                List<Integer> allergens = getAllergens(elem);
                if (StringUtils.isBlank(category)) {
                    paragraphIndex++;
                    category = previousCategory;
                } else {
                    rowIndex++;
                    paragraphIndex = 1;
                    previousCategory = category;
                }
                if (StringUtils.isNotBlank(description) && !prices.isEmpty()) {
                    result.add(Dish.of(getId(mensaId, baseDate, rowIndex, paragraphIndex), baseDate, mensaId, category, description, labels, prices, allergens));
                }
            }
        }
        return result;
    }

    @Override
    Stream<LocalDate> getBaseDate(Element element) {
        Stream<LocalDate> result = Stream.empty();
        String text = StringUtils.defaultString(element.text());
        try {
            result = Stream.of(LocalDate.parse(text, formatter));
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse base date from string: {}", text);
        }
        return result;

    }

    @Override
    String getTableSelector() {
        return "table";
    }
}
