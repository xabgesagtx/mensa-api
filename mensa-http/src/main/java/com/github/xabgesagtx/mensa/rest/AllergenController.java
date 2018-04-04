package com.github.xabgesagtx.mensa.rest;

import com.github.xabgesagtx.mensa.common.ResourceNotFoundException;
import com.github.xabgesagtx.mensa.model.Allergen;
import com.github.xabgesagtx.mensa.persistence.AllergenRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for allergens
 */
@RestController
@RequestMapping("rest/allergen")
@Api(tags = "allergen", description = "All info to allergens")
public class AllergenController {

    @Autowired
    private AllergenRepository repo;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Allergen> all() {
        return repo.findAllByOrderByNumberAsc();
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Allergen byId(@PathVariable("id") Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No allergen with id " + id));
    }
}
