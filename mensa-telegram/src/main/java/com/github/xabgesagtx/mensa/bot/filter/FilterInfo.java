package com.github.xabgesagtx.mensa.bot.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Class represents the filter information stored in a filter button
 */
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@Slf4j
public class FilterInfo {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private String value;
    private String mensaId;
    private LocalDate date;

    public String toString() {
        return String.format("%s,%s,%s", value, mensaId, DATE_TIME_FORMATTER.format(date));
    }

    public static Optional<FilterInfo> fromString(String text) {
        Optional<FilterInfo> result = Optional.empty();
        String[] parts = StringUtils.split(text, ',');
        if (parts != null && parts.length == 3) {
            String value = parts[0];
            String mensaId = parts[1];
            String dateString = parts[2];
            try {
                LocalDate date = LocalDate.parse(dateString, DATE_TIME_FORMATTER);
                result = Optional.of(FilterInfo.of(value, mensaId, date));
            } catch (DateTimeParseException e) {
                log.info("Failed to parse local date from \"{}\": {}", dateString, e.getMessage());
            }
        }
        return result;
    }
}
