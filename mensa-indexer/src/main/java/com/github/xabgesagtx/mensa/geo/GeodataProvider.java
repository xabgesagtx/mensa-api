package com.github.xabgesagtx.mensa.geo;

import com.github.xabgesagtx.mensa.config.ScrapeConfig;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides geocoding for mensa locations. Keeps a cache to avoid frequent queries. Nominatim doesn't like that.
 */
@Component
@Slf4j
public class GeodataProvider {

	private static final String Q_PARAM = "q";
	private static final String FORMAT_PARAM = "format";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MensaRepository mensaRepo;

	@Autowired
	private ScrapeConfig config;

	private Map<String, Point> geoDataCache = new ConcurrentHashMap<>();

	private final RateLimiter rateLimiter = RateLimiter.create(1);

	public Optional<Point> search(String address, String zipcode, String city) {
		return search(createQueryString(address, zipcode, city));
	}

	public Optional<Point> search(String query) {
		return Optional.ofNullable(geoDataCache.computeIfAbsent(query, key -> searchInt(query).orElse(null)));
	}

	private Optional<Point> searchInt(String query) {
		Optional<Point> result = Optional.empty();
		String uri = UriComponentsBuilder.fromHttpUrl(config.getNominatinUrl())
				.queryParam(Q_PARAM, query)
				.queryParam(FORMAT_PARAM, "json")
				.build()
				.toUriString();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.USER_AGENT, "Mensa Api Hamburg");
		HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);
		try {
			rateLimiter.acquire();
			ResponseEntity<List<NominatimResult>> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<NominatimResult>>() {});
			if (response.getStatusCode().is2xxSuccessful()) {
				List<NominatimResult> body = response.getBody();
				if (body.isEmpty()) {
					log.info("No results found for query \"{}\"", query);
				} else {
					result = body.stream().map(item -> new Point(item.getLon(), item.getLat())).findFirst();
				}
			} else {
				log.error("Wrong status code for query \"{}\": {} - ", query, response.getStatusCodeValue(), response.getStatusCode().getReasonPhrase());
			}
		} catch (RestClientException e) {
			log.error("Failed to get geodata for \"{}\": {}", query, e.getMessage());
		}
		return result;
	}

	@PostConstruct
	public void start() {
		mensaRepo.findAll().stream().filter(mensa -> mensa.getPoint() != null).forEach(mensa -> geoDataCache.put(createQueryString(mensa.getAddress(), mensa.getZipcode(), mensa.getCity()), mensa.getPoint()));
	}

	private String createQueryString(String address, String zipcode, String city) {
		return String.format("%s, %s %s, Germany", address, zipcode, city);
	}
}
