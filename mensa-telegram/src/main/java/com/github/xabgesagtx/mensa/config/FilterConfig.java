package com.github.xabgesagtx.mensa.config;

import com.github.xabgesagtx.mensa.bot.filter.FilterType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Configuration class for a filter
 */
@Getter
@Setter
public class FilterConfig {

    private String messageKey;
    private String value;
    private FilterType type;
    private List<String> labelsToFilter;

}
