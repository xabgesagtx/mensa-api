package com.github.xabgesagtx.mensa.bot.date;


import com.github.xabgesagtx.mensa.bot.date.utils.TodaySupplier;
import com.github.xabgesagtx.mensa.bot.messages.Messages;
import com.github.xabgesagtx.mensa.bot.messages.MessagesService;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.time.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Supplies the result for a today command
 */
@Component
public class TodayResultSupplier implements IDateSearchResultSupplier {

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private TodaySupplier supplier;

    @Autowired
    private TimeUtils timeUtils;

    @Override
    public Optional<DateSearchResult> forTextAndMensa(String text, Mensa mensa) {
        Optional<DateSearchResult> result = Optional.empty();
        if (StringUtils.equalsIgnoreCase(text, messagesService.getMessage(Messages.COMMAND_DATE_TODAY))) {
            LocalDate today = supplier.get();
            if (timeUtils.isOpeningDay(today, mensa)) {
                result = Optional.of(DateSearchResult.of(today, Optional.empty()));
            } else {
                result = Optional.of(DateSearchResult.of(today, Optional.of(timeUtils.nextOpeningDay(today, mensa))));
            }
        }
        return result;
    }
}
