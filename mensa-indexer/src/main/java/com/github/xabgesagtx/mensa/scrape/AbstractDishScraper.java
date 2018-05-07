package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.utils.WebUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base class for scraping dishes
 */
@Slf4j
abstract class AbstractDishScraper {

    private static final DateTimeFormatter ID_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d+,\\d{2})");
    protected final DecimalFormat decimalFormat;

    @Autowired
    private WebUtils utils;

    private final IntegerAwareStringComparator comparator = new IntegerAwareStringComparator();

    AbstractDishScraper() {
        decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
        decimalFormat.setParseBigDecimal(true);
    }

    /**
     * Scrape dishes
     * @param url to scrape dishes from
     * @param mensaId of the mensa the dishes
     * @return the scraped dishes
     */
    public List<Dish> scrape(String url, String mensaId) {
        return utils.getDocumentAsString(url).map(text -> scrapeFromString(text,mensaId)).orElseGet(Lists::newArrayList);
    }

    @VisibleForTesting
    List<Dish> scrapeFromString(String documentAsString, String mensaId) {
        Document document = Jsoup.parse(documentAsString);
        return scrapeFromDocument(document, mensaId);
    }

    private List<Dish> scrapeFromDocument(Document document, String mensaId) {
        List<Dish> result = Lists.newArrayList();
        Optional<Element> weekMenuTable = document.select(getTableSelector()).stream().findFirst();
        if (weekMenuTable.isPresent()) {
            Optional<LocalDate> baseDate = weekMenuTable.get().select("tr th.category").stream().flatMap(this::getBaseDate).findFirst();
            Elements rows = weekMenuTable.get().select("tbody tr");
            if (baseDate.isPresent() && !rows.isEmpty()) {
                result = scrapeFromTable(baseDate.get(), rows, mensaId);
            }
        }
        return result;
    }

    protected String getId(String mensaId, LocalDate date, int rowIndex, int paragraphIndex) {
        return String.format("%s_%s_%s_%s", mensaId, date.format(ID_DATE_TIME_FORMATTER), rowIndex, paragraphIndex);
    }

    protected List<BigDecimal> getPricesFromString(String text) {
        List<BigDecimal> result = Lists.newArrayList();
        Matcher matcher = PRICE_PATTERN.matcher(text);
        while (matcher.find()) {
            getBigDecimalFromString(matcher.group(1)).ifPresent(result::add);
        }
        return result;
    }

    private Optional<BigDecimal> getBigDecimalFromString(String text) {
        Optional<BigDecimal> result = Optional.empty();
        try {
            result = Optional.of((BigDecimal)decimalFormat.parseObject(text));
        } catch (ParseException | NumberFormatException e) {
            log.warn("Failed to parse price from string: \"{}\"", text);
        }
        return result;
    }

    /**
     * Get base date from a specific cell
     * @param cell to get date from
     * @return date if available, empty stream otherwise
     */
    abstract Stream<LocalDate> getBaseDate(Element cell);

    /**
     * Scrape dishes from table rows
     * @param baseDate to be used for the dishes
     * @param rows to scrape dishes from
     * @param mensaId id of the mensa the dishes are from
     * @return list of dishes
     */
    abstract List<Dish> scrapeFromTable(LocalDate baseDate, Elements rows, String mensaId);

    protected List<String> getAllergens(Element cell) {
        return cell.select("span.tooltip").stream().map(Element::text).filter(StringUtils::isNotBlank).map(String::trim).distinct().sorted(comparator).collect(Collectors.toList());
    }

    protected List<String> getLabels(Element cell) {
        return cell.select("img").stream().map(img -> img.attr("title")).filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    abstract String getTableSelector();
}
