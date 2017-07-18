package com.github.xabgesagtx.mensa.scrape;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.xabgesagtx.mensa.model.Dish;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SingleDayScraperTest {

    private SingleDayScraper scraper = new SingleDayScraper();

    @Test
    public void testScrapeFromDocumentEmpty() throws IOException {
        String htmlString = "<html></html>";
        List<Dish> expected = Lists.newArrayList();
        assertThat(scraper.scrapeFromString(htmlString, "100"), equalTo(expected));
    }

    @Test
    public void testScrapeFromDocument() throws IOException {
        String htmlString = TestUtils.readStringFromClasspath("/scraping/single_day.html");
        List<Dish> expected = TestUtils.readObjectFromClasspath("/scraping/single_day.json", new TypeReference<List<Dish>>() {});
        List<Dish> was = scraper.scrapeFromString(htmlString, "100");
        assertThat(was, equalTo(expected));
    }

}