package com.github.xabgesagtx.mensa.update;

import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.bus.Event;
import reactor.bus.EventBus;

import java.util.List;

/**
 * Triggers update jobs for menus of all mensas in the database
 */
@Component
@Slf4j
public class AllMenuUpdateStarter {

	@Autowired
	private MensaRepository mensaRepo;
	
	@Autowired
	private EventBus bus;
	
	@Scheduled(cron = "${update.menu.cron}")
	public void startUpdates() {
		List<Mensa> mensas = mensaRepo.findAll();
		log.info("Starting update of menus for {} mensas", mensas.size());
		mensas.forEach(mensa -> bus.notify("mensa_menu_update", Event.wrap(mensa)));
	}

}
