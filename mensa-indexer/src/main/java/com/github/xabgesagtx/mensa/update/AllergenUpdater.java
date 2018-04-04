package com.github.xabgesagtx.mensa.update;

import com.github.xabgesagtx.mensa.model.Allergen;
import com.github.xabgesagtx.mensa.persistence.AllergenRepository;
import com.github.xabgesagtx.mensa.scrape.AllergenScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Updater to trigger scraping of allergens and replacing the ones saved so far
 */
@Service
@Slf4j
public class AllergenUpdater {

    @Autowired
    private AllergenScraper scraper;

    @Autowired
    private AllergenRepository repo;

    @Scheduled(cron = "${update.allergen.cron}")
    public void update() {
        log.info("Starting allergen update");
        List<Allergen> allergens = scraper.scrape();
        if (!allergens.isEmpty()) {
            List<Allergen> oldAllergens = repo.findAll();
            log.info("Replacing {} old allergens with {} new ones", oldAllergens.size(), allergens.size());
            repo.deleteAll(oldAllergens);
            repo.saveAll(allergens);
            log.info("Finished allergen update");
        } else {
            log.info("No allergens found");
        }
    }

}
