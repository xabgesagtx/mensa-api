package com.github.xabgesagtx.mensa.web;

import com.github.xabgesagtx.mensa.model.Allergen;
import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.web.cache.AllergenCache;
import com.github.xabgesagtx.mensa.web.cache.LabelCache;
import com.github.xabgesagtx.mensa.web.cache.MensaCache;
import com.github.xabgesagtx.mensa.web.dto.DishWebDTO;
import com.github.xabgesagtx.mensa.web.dto.LabelWebDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  Factory to create dish web dtos
 */
@Component
public class DishWebDTOFactory {

    @Autowired
    private LabelCache labelCache;

    @Autowired
    private AllergenCache allergenCache;

    @Autowired
    private MensaCache mensaCache;

    /**
     * Create DTO from a dish
     * @param dish to create dto from
     * @return the dto, cannot return null
     */
    public DishWebDTO create(Dish dish) {
        List<LabelWebDTO> labels = dish.getLabels().stream().map(this::toLabelWebDTO).collect(Collectors.toList());
        List<Allergen> allergens = dish.getAllergens().stream().map(allergenCache::getAllergen).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        Mensa mensa = mensaCache.getMensa(dish.getMensaId()).orElse(null);
        return DishWebDTO.of(dish.getId(), dish.getDate(), dish.getDescription(), dish.getCategory(), allergens, labels, dish.getPrices(), mensa);
    }

    private LabelWebDTO toLabelWebDTO(String name) {
        LabelWebDTO.LabelWebDTOBuilder builder = LabelWebDTO.builder().name(name);
        labelCache.getLabel(name).ifPresent(label -> builder.imageUrl(label.getImageUrl()));
        return builder.build();
    }

}
