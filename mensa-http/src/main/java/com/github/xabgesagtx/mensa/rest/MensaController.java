package com.github.xabgesagtx.mensa.rest;


import com.github.xabgesagtx.mensa.common.ResourceNotFoundException;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for mensas
 */
@RestController
@RequestMapping("rest/mensa")
@Api(tags = "mensa", description = "All info to mensas")
public class MensaController {

    @Autowired
    private MensaRepository repo;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Mensa> all() {
        return repo.findAll();
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mensa findById(@PathVariable("id") String id) {
        Mensa result = repo.findOne(id);
        if (result == null) {
            throw new ResourceNotFoundException("No mensa with id " + id);
        } else {
            return result;
        }
    }
}
