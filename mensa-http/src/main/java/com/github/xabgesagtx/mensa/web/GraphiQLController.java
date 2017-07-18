package com.github.xabgesagtx.mensa.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the graphiql page
 */
@Controller
@RequestMapping("graphiql")
public class GraphiQLController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String graphiql() {
        return "graphiql";
    }
}
