package com.github.xabgesagtx.mensa.rest;

import com.github.xabgesagtx.mensa.common.ResourceNotFoundException;
import com.github.xabgesagtx.mensa.model.Label;
import com.github.xabgesagtx.mensa.persistence.LabelRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for labels
 */
@RestController
@RequestMapping("rest/label")
@Api(tags = "label", description = "All info to label")
public class LabelController {

    @Autowired
    private LabelRepository repo;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Label> all() {
        return repo.findAll();
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Label byId(@PathVariable("id") String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No label with id " + id));
    }

}
