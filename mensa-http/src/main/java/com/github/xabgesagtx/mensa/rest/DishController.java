package com.github.xabgesagtx.mensa.rest;

import com.github.xabgesagtx.mensa.common.ResourceNotFoundException;
import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.persistence.DishRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Rest controller for dishes
 */
@RestController
@RequestMapping("rest/dish")
@Api(tags = "dish", description = "All info to dishes")
public class DishController {

    @Autowired
    private DishRepository repo;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Dish> findDishes(@RequestParam(value = "mensaId") String mensaId, @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return repo.findByDateAndMensaId(date,mensaId);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Dish byId(@PathVariable("id") String id) {
        Dish result = repo.findOne(id);
        if (result == null) {
            throw new ResourceNotFoundException("No dish with id " + id);
        } else {
            return result;
        }
    }
}
