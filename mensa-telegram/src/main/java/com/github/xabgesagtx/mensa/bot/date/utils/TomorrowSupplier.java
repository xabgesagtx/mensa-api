package com.github.xabgesagtx.mensa.bot.date.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Supplier;

/**
 * Supplies the date of tomorrow
 */
@Component
public class TomorrowSupplier implements Supplier<LocalDate> {

    @Override
    public LocalDate get() {
        return LocalDate.now().plusDays(1);
    }

}
