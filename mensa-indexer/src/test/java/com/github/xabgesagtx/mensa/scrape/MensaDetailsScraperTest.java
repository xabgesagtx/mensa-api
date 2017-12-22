package com.github.xabgesagtx.mensa.scrape;

import com.github.xabgesagtx.mensa.scrape.model.MensaDetails;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MensaDetailsScraperTest {

	private MensaDetailsScraper scraper = new MensaDetailsScraper();

	@Test
	public void scrapeFromDocumentWithComma() throws IOException {
		String htmlString = TestUtils.readStringFromClasspath("/scraping/single_day.html");
		Optional<MensaDetails> expected = Optional.of(MensaDetails.builder().address("Von-Melle-Park 6").zipcode("20146").city("Hamburg").build());
		Optional<MensaDetails> was = scraper.scrapeFromString(htmlString, "100");
		assertThat(was, equalTo(expected));
	}

	@Test
	public void scrapeFromDocumentWithoutComma() throws IOException {
		String htmlString = TestUtils.readStringFromClasspath("/scraping/mensa_details.html");
		Optional<MensaDetails> expected = Optional.of(MensaDetails.builder().address("Überseeallee 16").zipcode("20457").city("Hamburg").build());
		Optional<MensaDetails> was = scraper.scrapeFromString(htmlString, "100");
		assertThat(was, equalTo(expected));
	}

}