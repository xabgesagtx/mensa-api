package com.github.xabgesagtx.mensa.web.dto;


import com.github.xabgesagtx.mensa.model.Allergen;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO of a dish for displaying it on a web page
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class DishWebDTO {

    private String id;
    private String description;
    private String category;
    private List<Allergen> allergens;
    private List<LabelWebDTO> labels;
    private List<BigDecimal> prices;
    public String getPricesString() {
        return prices.stream().map(price -> String.format("%,.2f €", price)).collect(Collectors.joining(" / "));
    }

}
