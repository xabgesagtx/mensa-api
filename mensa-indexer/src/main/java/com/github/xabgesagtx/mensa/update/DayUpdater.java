package com.github.xabgesagtx.mensa.update;


import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.persistence.DishRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Updater to replace old dishes for a day and replace them with new dishes
 */
@Service
@Slf4j
public class DayUpdater implements Consumer<Event<DayUpdate>> {
	
	@Autowired
	private DishRepository repo;
	
	@Autowired
	private EventBus bus;

	@Override
	@Transactional
	public void accept(Event<DayUpdate> event) {
		DayUpdate update = event.getData();
		log.info("Starting update for day {} and mensa {}", update.getDay().format(DateTimeFormatter.ISO_DATE), update.getMensaId());
		List<Dish> oldDishes = repo.findByDateAndMensaId(update.getDay(), update.getMensaId());
		repo.deleteAll(oldDishes);
		repo.saveAll(update.getDishes());
		log.info("Finished update for day {} and mensa {}. New: {}, Old: {}", update.getDay().format(DateTimeFormatter.ISO_DATE), update.getMensaId(), update.getDishes().size(), oldDishes.size());
	}
	
}
