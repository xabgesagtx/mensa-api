package com.github.xabgesagtx.mensa.web.cache;

import com.github.xabgesagtx.mensa.model.Allergen;
import com.github.xabgesagtx.mensa.persistence.AllergenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Cache to hold all available allergens
 */
@Component
public class AllergenCache {

    @Autowired
    private AllergenRepository repo;

    private List<Allergen> allergens = new ArrayList<>();

    /**
     * Get allergen from cache
     * @param number of the allergen
     * @return allergen if available, empty otherwise
     */
    public Optional<Allergen> getAllergen(String number) {
        synchronized (allergens) {
            return allergens.stream().filter(allergen -> allergen.getNumber().equals(number)).findFirst();
        }
    }

    /**
     * Scheduled job to reload allergens from db
     */
    @Scheduled(cron = "${cache.allergens.cron}")
    public void update() {
        List<Allergen> newAllergens = repo.findAllByOrderByNumberAsc();
        synchronized (allergens) {
            allergens.clear();
            allergens.addAll(newAllergens);
        }
    }

    /**
     * Load cache on startup
     */
    @PostConstruct
    public void start() {
        update();
    }

}
