package com.github.xabgesagtx.mensa.bot.date;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Wrapper class to hold the search result for a specific date
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class DateSearchResult {

    private final LocalDate date;
    private final Optional<LocalDate> alternativeDate;

    public LocalDate getDateToUse() {
        return alternativeDate.orElse(date);
    }

}
