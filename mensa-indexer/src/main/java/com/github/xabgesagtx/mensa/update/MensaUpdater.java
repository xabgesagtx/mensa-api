package com.github.xabgesagtx.mensa.update;

import com.github.xabgesagtx.mensa.config.ScrapeConfig;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import com.github.xabgesagtx.mensa.scrape.MensaScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scrapes all available mensas and updates the information available for them
 */
@Component
@Slf4j
public class MensaUpdater {
	
	@Autowired
	private MensaRepository repo;
	
	@Autowired
	private MensaScraper scraper;

	@Scheduled(cron = "${update.mensa.cron}")
	public void update() {
		log.info("Starting update of mensas");
		scraper.scrape().forEach(this::updateMensa);
		log.info("Finished update of mensas");
	}
	
	private void updateMensa(Mensa mensa) {
		log.info("Updating mensa {} ({})", mensa.getName(), mensa.getId());
		repo.save(mensa);
	}

}
