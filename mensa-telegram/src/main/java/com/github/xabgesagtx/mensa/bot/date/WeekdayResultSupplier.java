package com.github.xabgesagtx.mensa.bot.date;


import com.github.xabgesagtx.mensa.config.BotConstants;
import com.github.xabgesagtx.mensa.bot.date.utils.TodaySupplier;
import com.github.xabgesagtx.mensa.model.Mensa;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Supplies the result for a string that represents a day of week
 */
@Component
public class WeekdayResultSupplier implements IDateSearchResultSupplier {

    @Autowired
    private TodaySupplier todaySupplier;

    @Override
    public Optional<DateSearchResult> forTextAndMensa(String text, Mensa mensa) {
        return getDayOfWeekFromString(text)
                .map(dayOfWeek -> getFittingDateForDayOfWeek(dayOfWeek, todaySupplier.get()))
                .map(date -> DateSearchResult.of(date, Optional.empty()));
    }

    LocalDate getFittingDateForDayOfWeek(DayOfWeek dayOfWeek, LocalDate date) {
        LocalDate result;
        DayOfWeek currentDayOfWeek = date.getDayOfWeek();
        int diff = dayOfWeek.getValue() - currentDayOfWeek.getValue();
        if (diff >= 0) {
            result = date.plusDays(diff);
        } else {
            result = date.plusWeeks(1).plusDays(diff);
        }
        return result;
    }

    Optional<DayOfWeek> getDayOfWeekFromString(String text) {
        return Arrays.stream(DayOfWeek.values())
                .filter(dayOfWeek -> matchesDayOfWeek(dayOfWeek, text, BotConstants.DEFAULT_LOCALE))
                .findFirst();
    }

    private boolean matchesDayOfWeek(DayOfWeek dayOfWeek, String text, Locale locale) {
        return Stream.of(TextStyle.FULL,TextStyle.SHORT).anyMatch(style -> StringUtils.equalsIgnoreCase(dayOfWeek.getDisplayName(style,locale),text));
    }
}
