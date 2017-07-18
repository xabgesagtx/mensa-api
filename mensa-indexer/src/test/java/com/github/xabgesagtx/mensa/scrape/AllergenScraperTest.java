package com.github.xabgesagtx.mensa.scrape;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.xabgesagtx.mensa.model.Allergen;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AllergenScraperTest {

    private AllergenScraper scraper = new AllergenScraper();

    @Test
    public void testScrapeFromString() throws Exception {
        String htmlString = TestUtils.readStringFromClasspath("/scraping/allergenAndLabel.html");
        List<Allergen> expected = TestUtils.readObjectFromClasspath("/scraping/allergens.json", new TypeReference<List<Allergen>>(){});
        List<Allergen> was = scraper.scrapeFromString(htmlString, "http://example.com/");
        assertThat(was, equalTo(expected));
    }

}