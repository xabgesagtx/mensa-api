package com.github.xabgesagtx.mensa.graphql;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.DishRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolver for dishes in a graphql query
 */
@Component
@Slf4j
public class DishesResolver implements GraphQLResolver<Mensa> {

    @Autowired
    private DishRepository repo;

    public List<Dish> dishes(Mensa mensa, String dateISOString) {
        List<Dish> result = new ArrayList<>();
        try {
            LocalDate date = LocalDate.parse(dateISOString, DateTimeFormatter.ISO_DATE);
            result = repo.findByDateAndMensaId(date, mensa.getId());
        } catch (DateTimeParseException e) {
            log.info("Failed to parse date string {}: {}", dateISOString, e.getMessage());
        }
        return result;
    }

}
