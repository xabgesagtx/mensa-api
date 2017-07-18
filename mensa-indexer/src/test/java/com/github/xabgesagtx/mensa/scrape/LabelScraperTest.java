package com.github.xabgesagtx.mensa.scrape;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.xabgesagtx.mensa.model.Label;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class LabelScraperTest {

    private LabelScraper scraper = new LabelScraper();

    @Test
    public void scrapeFromString() throws IOException {
        String htmlString = TestUtils.readStringFromClasspath("/scraping/allergenAndLabel.html");
        List<Label> expected = TestUtils.readObjectFromClasspath("/scraping/labels.json", new TypeReference<List<Label>>(){});
        List<Label> was = scraper.scrapeFromString(htmlString, "http://example.com/");
        assertThat(was, equalTo(expected));
    }

}