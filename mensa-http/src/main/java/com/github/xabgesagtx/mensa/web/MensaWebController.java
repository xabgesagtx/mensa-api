package com.github.xabgesagtx.mensa.web;

import com.github.xabgesagtx.mensa.common.ResourceNotFoundException;
import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.model.QDish;
import com.github.xabgesagtx.mensa.persistence.DishRepository;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import com.github.xabgesagtx.mensa.time.TimeUtils;
import com.github.xabgesagtx.mensa.web.dto.DishWebDTO;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for mensa overview and detail pages that display the dishes
 */
@Controller
@RequestMapping("")
public class MensaWebController {

    @Autowired
    private MensaRepository mensaRepo;

    @Autowired
    private DishWebDTOFactory dishDtoFactory;

    @Autowired
	private MongoTemplate template;

    @Autowired
    private DishRepository dishRepo;

    @Autowired
    private TimeUtils timeUtils;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView allMensas(ModelAndView modelAndView) {
        modelAndView.addObject("mensas", mensaRepo.findAllByOrderByName());
        modelAndView.setViewName("mensas");
        return modelAndView;
    }

    @RequestMapping(value = "mensa/{id}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView nextOpeningDay(@PathVariable("id") String id, ModelAndView modelAndView) {
        Mensa mensa = findMensaWithNullCheck(id);
        return renderMenu(mensa, timeUtils.nextOpeningDay(mensa), modelAndView);
    }

    private Mensa findMensaWithNullCheck(String id) {
        return mensaRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No mensa with id " + id));
    }

    @RequestMapping(value = "mensa/{id}/{date}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView day(@PathVariable("id") String id, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, ModelAndView modelAndView) {
        Mensa mensa = findMensaWithNullCheck(id);
        return renderMenu(mensa, date, modelAndView);
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public ModelAndView findDishes(@QuerydslPredicate(root = Dish.class) Predicate predicate, @PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable, ModelAndView modelAndView) {
		Sort realSort = pageable.getSort().and(new Sort(Sort.Direction.ASC, "id"));
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), realSort);
		Page<Dish> page = dishRepo.findAll(predicate, pageRequest);
		List<DishWebDTO> dishes = page.getContent().stream().map(dishDtoFactory::create).collect(Collectors.toList());
		modelAndView.addObject("page", page);
		modelAndView.addObject("dishes", dishes);
		modelAndView.addObject("labels", getLabelsFromDishes());
		modelAndView.addObject("categories", getCategoriesFromDishes());
		modelAndView.addObject("mensas", mensaRepo.findAllByOrderByName());
        modelAndView.setViewName("search");
        return modelAndView;
    }

    private ModelAndView renderMenu(Mensa mensa, LocalDate date, ModelAndView modelAndView) {
        modelAndView.addObject("mensa", mensa);
        modelAndView.addObject("date", DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy", Locale.ENGLISH).format(date));
        List<Dish> dishesFromRepo = dishRepo.findByDateAndMensaIdOrderByIdAsc(date, mensa.getId());
        List<String> categories = dishesFromRepo.stream().map(Dish::getCategory).distinct().collect(Collectors.toList());
        modelAndView.addObject("categories", categories);
        List<DishWebDTO> dishes = dishesFromRepo.stream().map(dishDtoFactory::create).collect(Collectors.toList());
        Map<String,List<DishWebDTO>> dishesMap = dishes.stream().collect(Collectors.groupingBy(DishWebDTO::getCategory, Collectors.toList()));
        modelAndView.addObject("dishesMap", dishesMap);
        modelAndView.addObject("next", timeUtils.nextOpeningDay(date.plusDays(1), mensa));
        modelAndView.addObject("previous", timeUtils.lastOpeningDay(date.minusDays(1), mensa));
        modelAndView.setViewName("menu");
        return modelAndView;
    }

    List<String> getLabelsFromDishes() {
    	List<String> labels = new ArrayList<>();
    	template.getCollection("dish").distinct("labels", String.class).iterator().forEachRemaining(labels::add);
		Collections.sort(labels, String.CASE_INSENSITIVE_ORDER);
		return labels;
	}

	List<String> getCategoriesFromDishes() {
    	List<String> categories = new ArrayList<>();
		template.getCollection("dish").distinct(QDish.dish.category.getMetadata().getName(), String.class).iterator().forEachRemaining(categories::add);
		Collections.sort(categories, String.CASE_INSENSITIVE_ORDER);
		return categories;
	}



}
