package com.github.xabgesagtx.mensa.update;

import com.github.xabgesagtx.mensa.model.Label;
import com.github.xabgesagtx.mensa.persistence.LabelRepository;
import com.github.xabgesagtx.mensa.scrape.LabelScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Get new labels and replace the old labels in the database
 */
@Component
@Slf4j
public class LabelUpdater {

    @Autowired
    private LabelScraper scraper;

    @Autowired
    private LabelRepository repo;

    @Scheduled(cron = "${update.label.cron}")
    public void update() {
        log.info("Starting label update");
        List<Label> labels = scraper.scrape();
        labels.forEach(label -> label.setId(UUID.randomUUID().toString()));
        if (!labels.isEmpty()) {
            List<Label> oldLabels = repo.findAll();
            log.info("Replacing {} old labels with {} new ones", oldLabels.size(), labels.size());
            repo.deleteAll(oldLabels);
            repo.saveAll(labels);
            log.info("Finished label update");
        } else {
            log.info("No labels found");
        }
    }

}
