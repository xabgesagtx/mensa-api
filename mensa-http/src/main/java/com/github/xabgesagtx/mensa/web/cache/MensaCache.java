package com.github.xabgesagtx.mensa.web.cache;

import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
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
public class MensaCache {

    @Autowired
    private MensaRepository repo;

    private List<Mensa> mensas = new ArrayList<>();

    /**
     * Get mensa from cache
     * @param id of the mensa
     * @return mensa if available, empty otherwise
     */
    public Optional<Mensa> getMensa(String id) {
        synchronized (mensas) {
            return mensas.stream().filter(allergen -> allergen.getId().equals(id)).findFirst();
        }
    }

    /**
     * Scheduled job to reload mensas from db
     */
    @Scheduled(cron = "${cache.allergens.cron}")
    public void update() {
        List<Mensa> newMensas = repo.findAllByOrderByName();
        synchronized (mensas) {
            mensas.clear();
            mensas.addAll(newMensas);
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
