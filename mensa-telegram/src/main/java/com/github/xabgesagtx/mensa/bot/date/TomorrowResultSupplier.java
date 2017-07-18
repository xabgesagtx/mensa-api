package com.github.xabgesagtx.mensa.bot.date;

import com.github.xabgesagtx.mensa.bot.date.utils.TomorrowSupplier;
import com.github.xabgesagtx.mensa.bot.messages.Messages;
import com.github.xabgesagtx.mensa.bot.messages.MessagesService;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.time.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Supplies the result for a tomorrow command
 */
@Component
public class TomorrowResultSupplier implements IDateSearchResultSupplier {

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private TomorrowSupplier supplier;

    @Autowired
    private TimeUtils timeUtils;

    private List<String> todayStrings = Arrays.asList("morgen", "tomorrow");

    @Override
    public Optional<DateSearchResult> forTextAndMensa(String text, Mensa mensa) {
        Optional<DateSearchResult> result = Optional.empty();
        if (StringUtils.equalsIgnoreCase(text, messagesService.getMessage(Messages.COMMAND_DATE_TOMORROW))) {
            LocalDate tomorrow = supplier.get();
            if (timeUtils.isOpeningDay(tomorrow, mensa)) {
                result = Optional.of(DateSearchResult.of(tomorrow, Optional.empty()));
            } else {
                result = Optional.of(DateSearchResult.of(tomorrow, Optional.of(timeUtils.nextOpeningDay(tomorrow, mensa))));
            }
        }
        return result;
    }

}
