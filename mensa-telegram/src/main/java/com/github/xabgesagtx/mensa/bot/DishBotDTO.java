package com.github.xabgesagtx.mensa.bot;

import com.github.xabgesagtx.mensa.model.Dish;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO to be used for displaying the dish in a bot response
 */
@Getter
@AllArgsConstructor
class DishBotDTO {

    private final String category;
    private final String description;
    private final List<String> labels;
    private final String prices;

    static DishBotDTO fromDish(Dish dish) {
        String prices = dish.getPrices().stream().map(price -> String.format("%,.2f €", price)).collect(Collectors.joining(" / "));
        return new DishBotDTO(dish.getCategory(), dish.getDescription(), dish.getLabels(), prices);
    }

}
