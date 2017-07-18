package com.github.xabgesagtx.mensa.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Utility component for accessing web content
 */
@Component
@Slf4j
public class WebUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);
	
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Get content of a website as a string
	 * @param url of the website
	 * @return the content if available, empty otherwise
	 */
	public Optional<String> getDocumentAsString(String url) {
		Optional<String> result = Optional.empty();
		try {
			ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
			if (entity.getStatusCode().is2xxSuccessful()) {
				String body = entity.getBody();
				if (StringUtils.isBlank(body)) {
					logger.error("Empty response for {}", url);
				} else {
					result = Optional.of(body);
				}
			} else {
				logger.error("Failed to get {}. Response error: {}", url, entity.getStatusCode());
			}
		} catch (RestClientException e) {
			logger.error("Failed to get {} due to error: {}", url, e.getMessage());
		}
		return result;
	}

	/**
	 * Get the base url from a valid url
	 * @param url to get the base url from
	 * @return the base url if available, empty otherwise
	 */
	public Optional<String> getBaseUrl(String url) {
		Optional<String> result = Optional.empty();
		try {
			URI uri = new URI(url);
			String scheme = uri.getScheme();
			String host = uri.getHost();
			if (StringUtils.isNotBlank(scheme) && StringUtils.isNotBlank(host)) {
			    result = Optional.of(scheme + "://" + host + "/");
            } else {
			    logger.warn("Failed to get baseUrl from {} because scheme (\"{}\") or host (\"{}\") could not be read", url, scheme, host);
            }
		} catch (URISyntaxException e) {
            logger.warn("Failed to get baseUrl from {} due to error: {}", url, e.getMessage());
		}
		return result;
	}

}
