package com.github.xabgesagtx.mensa.scrape;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.xabgesagtx.mensa.geo.GeodataProvider;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.scrape.model.MensaDetails;
import com.github.xabgesagtx.mensa.scrape.model.MenuUrls;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.geo.Point;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MensaScraperTest {
	
	@InjectMocks
	private MensaScraper scraper;
	
	@Mock
	private MenuUrlScraper menuUrlScraper;

	@Mock
	private MensaDetailsScraper detailsScraper;

	@Mock
	private GeodataProvider geodataProvider;
	
	private final String URL_PREFIX = "http://example.com/";
	private final String TODAY_URL = URL_PREFIX + "today";
	private final String TOMORROW_URL = URL_PREFIX + "tomorrow";
	private final String THIS_WEEK_URL = URL_PREFIX + "thisWeek";
	private final String NEXT_WEEK_URL = URL_PREFIX + "nextWeek";
	private final String ADDRESS = "address";
	private final String CITY = "city";
	private final String ZIPCODE = "12345";
	
	@Before
	public void setUp() {
		MenuUrls urls = MenuUrls.of(TODAY_URL, TOMORROW_URL, THIS_WEEK_URL, NEXT_WEEK_URL);
		when(menuUrlScraper.scrape(anyString())).thenReturn(Optional.of(urls));
		MensaDetails details = MensaDetails.builder().city(CITY).zipcode(ZIPCODE).address(ADDRESS).build();
		when(detailsScraper.scrape(anyString())).thenReturn(Optional.of(details));
		when(geodataProvider.search(anyString(), anyString(), anyString())).thenReturn(Optional.of(new Point(1, 1)));
	}

	@Test
	public void testScrapeFromString() throws IOException {
		String html = TestUtils.readStringFromClasspath("/scraping/mensas.html");
		List<Mensa> was = scraper.scrapeFromString(html, URL_PREFIX);
		List<Mensa> expected = TestUtils.readObjectFromClasspath("/scraping/mensas.json", new TypeReference<List<Mensa>>() {});
		assertThat(was, equalToMensas(expected));
	}

	private Matcher<List<Mensa>> equalToMensas(List<Mensa> mensas) {
		return new BaseMatcher<List<Mensa>>() {
			@Override
			@SuppressWarnings("unchecked")
			public boolean matches(Object item) {
				List<Mensa> matchTargetList = (List<Mensa>) item;
				if (matchTargetList.size() != mensas.size()) {
					return false;
				} else {
					for (int i = 0; i < mensas.size(); i++) {
						Mensa mensa = mensas.get(i);
						Mensa matchTarget = matchTargetList.get(i);
						if (!EqualsBuilder.reflectionEquals(mensa, matchTarget, Arrays.asList("updatedAt"))) {
							return false;
						}
					}
					return true;
				}
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("each mensa should be equal except for the update value");
			}
		};
	}

	@Test
	public void testGetIdFromMensaUrl() {
		assertThat(scraper.getIdFromMensaUrl("http://hallo"), equalTo(Optional.empty()));
		assertThat(scraper.getIdFromMensaUrl("http://hallo/id/200/200"), equalTo(Optional.empty()));
		assertThat(scraper.getIdFromMensaUrl("http://hallo/id/200"), equalTo(Optional.of("200")));
		assertThat(scraper.getIdFromMensaUrl("http://hallo/id/200/"), equalTo(Optional.of("200")));
	}

}
