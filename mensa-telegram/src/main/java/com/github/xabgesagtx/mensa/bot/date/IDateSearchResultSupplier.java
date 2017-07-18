package com.github.xabgesagtx.mensa.bot.date;

import com.github.xabgesagtx.mensa.model.Mensa;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface for supplier that specify which date could be meant by looking at a specific string
 */
public interface IDateSearchResultSupplier {

    default Stream<DateSearchResult> forTextAndMensaAsStream(String text, Mensa mensa) {
        return forTextAndMensa(text, mensa).map(Stream::of).orElseGet(Stream::empty);
    }

    Optional<DateSearchResult> forTextAndMensa(String text, Mensa mensa);

}
