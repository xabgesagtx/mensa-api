package com.github.xabgesagtx.mensa.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the APIs page
 */
@Controller
@RequestMapping("apis")
public class ApisController {

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String apis() {
        return "apis";
    }
}
