package com.github.xabgesagtx.mensa.scrape;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.xabgesagtx.mensa.model.Dish;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MenuWeekScraperTest {
	
	private MenuWeekScraper scraper = new MenuWeekScraper();

	@Test
	public void testScrapeFromDocumentEmpty() throws IOException {
		String htmlString = "<html></html>";
		List<Dish> expected = Lists.newArrayList();
		assertThat(scraper.scrapeFromString(htmlString, "100"), equalTo(expected));
	}

	@Test
	public void testScrapeFromDocument() throws IOException {
		String htmlString = TestUtils.readStringFromClasspath("/scraping/whole_week.html");
		List<Dish> expected = TestUtils.readObjectFromClasspath("/scraping/whole_week.json", new TypeReference<List<Dish>>() {});
		List<Dish> was = scraper.scrapeFromString(htmlString, "100");
		assertThat(was, equalTo(expected));
	}
	
	@Test
	public void testGetBaseDateFromString() {
		Optional<LocalDate> was = scraper.getBaseDateFromString("Wochenplan: 05.06.2017 - 09.06.2017");
		Optional<LocalDate> expected = Optional.of(LocalDate.of(2017, 6, 5));
		assertThat(was, equalTo(expected));
	}

}
