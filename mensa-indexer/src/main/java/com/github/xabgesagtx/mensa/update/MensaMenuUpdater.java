package com.github.xabgesagtx.mensa.update;

import com.github.xabgesagtx.mensa.config.MensaConfig;
import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.scrape.MenuWeekScraper;
import com.github.xabgesagtx.mensa.scrape.SingleDayScraper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scrapes as many dishes as possible for a single mensa and trigger updates for each day with scraped dishes available
 */
@Service
@Slf4j
public class MensaMenuUpdater implements Consumer<Event<Mensa>> {
	
	@Autowired
	private MenuWeekScraper scraper;

	@Autowired
	private SingleDayScraper singleDayScraper;

	@Autowired
	private MensaConfig config;
	
	@Autowired
	private EventBus bus;
	
	@Override
	public void accept(Event<Mensa> event) {
		Mensa mensa = event.getData();
		log.info("Starting update of mensa {} ({})", mensa.getName(), mensa.getId());
		List<Dish> dishes = scraper.scrape(mensa.getThisWeekUrl(), mensa.getId());
		dishes.addAll(scraper.scrape(mensa.getNextWeekUrl(), mensa.getId()));
		if (config.isMensaOpenOnSaturday(mensa)) {
			dishes.addAll(scrapeSaturdayDishes(mensa));
		}
		log.info("Found total of {} dishes for mensa {} ({})", dishes.size(), mensa.getName(), mensa.getId());
		Map<LocalDate,List<Dish>> mapByDay = getMapByDay(dishes);
		mapByDay.entrySet().forEach(entry -> startDayUpdate(mensa.getId(), entry.getKey(), entry.getValue()));
	}

	private List<Dish> scrapeSaturdayDishes(Mensa mensa) {
		DayOfWeek today = LocalDate.now().getDayOfWeek();
		List<Dish> saturdayDishes = new ArrayList<>();
		if (today.equals(DayOfWeek.FRIDAY) && StringUtils.isNotBlank(mensa.getTomorrowUrl())) {
			saturdayDishes = singleDayScraper.scrape(mensa.getTomorrowUrl(), mensa.getId());
		} else if (today.equals(DayOfWeek.SATURDAY) && StringUtils.isNotBlank(mensa.getTodayUrl())) {
			saturdayDishes = singleDayScraper.scrape(mensa.getTodayUrl(), mensa.getId());
		}
		List<Dish> result = saturdayDishes.stream().filter(dish -> dish.getDate().getDayOfWeek().equals(DayOfWeek.SATURDAY)).collect(Collectors.toList());
		log.info("Scraped {} dishes for saturday for mensa {} ({})", result.size(), mensa.getName(), mensa.getId());
		return result;
	}

	private void startDayUpdate(String mensaId, LocalDate date, List<Dish> dishes) {
		log.info("Sending update request for mensa {} at {} with {} dishes", mensaId, date.format(DateTimeFormatter.ISO_DATE), dishes.size());
		bus.notify("day_update", Event.wrap(new DayUpdate(mensaId, date, dishes)));
	}
	
	private Map<LocalDate,List<Dish>> getMapByDay(List<Dish> dishes) {
		return dishes.stream().collect(Collectors.groupingBy(Dish::getDate));
	}
		
}
