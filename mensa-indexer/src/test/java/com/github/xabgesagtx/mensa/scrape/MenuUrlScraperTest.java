package com.github.xabgesagtx.mensa.scrape;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MenuUrlScraperTest {

	@Test
	public void testGetMenuUrlsFromString() throws IOException {
		MenuUrlScraper urlScraper = new MenuUrlScraper();
		String htmlString = TestUtils.readStringFromClasspath("/scraping/mensa.html");
		MenuUrls expected = TestUtils.readObjectFromClasspath("/scraping/mensa.json", new TypeReference<MenuUrls>(){});
		assertThat(urlScraper.scrapeFromString(htmlString, "http://example.com/").get(), equalTo(expected));
	}

}
