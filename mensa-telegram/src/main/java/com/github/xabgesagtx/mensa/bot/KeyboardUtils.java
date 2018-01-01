package com.github.xabgesagtx.mensa.bot;

import com.github.xabgesagtx.mensa.bot.filter.FilterInfo;
import com.github.xabgesagtx.mensa.bot.messages.Messages;
import com.github.xabgesagtx.mensa.bot.messages.MessagesService;
import com.github.xabgesagtx.mensa.config.BotConstants;
import com.github.xabgesagtx.mensa.config.FilterConfig;
import com.github.xabgesagtx.mensa.config.TelegramConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to create keyboards
 */
@Component
public class KeyboardUtils {

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private TelegramConfig config;

    /**
     * Creates the main select menu for selecting a date
     * @return the keyboard
     */
    public ReplyKeyboard createMainSelectKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(messagesService.getMessage(Messages.COMMAND_DATE_TODAY));
        firstRow.add(messagesService.getMessage(Messages.COMMAND_DATE_TOMORROW));
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(messagesService.getMessage(Messages.COMMAND_CHANGE_MENSA));
        secondRow.add(messagesService.getMessage(Messages.COMMAND_WEEKDAYS));
        KeyboardRow thirdRow = new KeyboardRow();
        KeyboardButton selectNearestMensaButton = new KeyboardButton(messagesService.getMessage(Messages.COMMAND_SELECT_NEAREST_MENSA));
        selectNearestMensaButton.setRequestLocation(true);
        thirdRow.add(selectNearestMensaButton);
        keyboard.setResizeKeyboard(true);
        keyboard.setKeyboard(Arrays.asList(firstRow, secondRow, thirdRow));
        return keyboard;
    }

    /**
     * Creates the select menu for selecting a day of week
     * @return the keyboard
     */
    public ReplyKeyboardMarkup createSelectDayOfWeekKeyboard(boolean withSaturday) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, BotConstants.DEFAULT_LOCALE));
        firstRow.add(DayOfWeek.TUESDAY.getDisplayName(TextStyle.FULL, BotConstants.DEFAULT_LOCALE));
        firstRow.add(DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.FULL, BotConstants.DEFAULT_LOCALE));
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(DayOfWeek.THURSDAY.getDisplayName(TextStyle.FULL, BotConstants.DEFAULT_LOCALE));
        secondRow.add(DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL, BotConstants.DEFAULT_LOCALE));
        if (withSaturday) {
            secondRow.add(DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL, BotConstants.DEFAULT_LOCALE));
        }
        secondRow.add(messagesService.getMessage(Messages.COMMAND_BACK));
        keyboard.setResizeKeyboard(true);
        keyboard.setKeyboard(Arrays.asList(firstRow, secondRow));
        return keyboard;
    }

    /**
     * Creates the keyboard for filtering of the dishes message
     * @param mensaId id of the mensa of the dishes
     * @param date of the dishes
     * @param selectedValue currently selected filter
     * @return the inline keyboard of the filters
     */
    public InlineKeyboardMarkup createFilterKeyboard(String mensaId, LocalDate date, String selectedValue) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();
        for (FilterConfig filter : config.getFilters()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            if (StringUtils.equals(selectedValue, filter.getValue())) {
                button.setText(messagesService.getMessage(Messages.FILTER_ALL));
                button.setCallbackData(FilterInfo.of(BotConstants.FILTER_ALL_VALUE, mensaId, date).toString());
            } else {
                button.setText(messagesService.getMessage(filter.getMessageKey()));
                button.setCallbackData(FilterInfo.of(filter.getValue(), mensaId, date).toString());
            }
            currentRow.add(button);
            if (currentRow.size() % 2 == 0) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }



}
