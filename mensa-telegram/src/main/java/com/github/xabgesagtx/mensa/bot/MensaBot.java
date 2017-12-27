package com.github.xabgesagtx.mensa.bot;

import com.github.xabgesagtx.mensa.bot.date.DateSearchResult;
import com.github.xabgesagtx.mensa.bot.date.IDateSearchResultSupplier;
import com.github.xabgesagtx.mensa.bot.filter.FilterInfo;
import com.github.xabgesagtx.mensa.bot.messages.Messages;
import com.github.xabgesagtx.mensa.bot.messages.MessagesService;
import com.github.xabgesagtx.mensa.config.BotConstants;
import com.github.xabgesagtx.mensa.config.MensaConfig;
import com.github.xabgesagtx.mensa.config.TelegramConfig;
import com.github.xabgesagtx.mensa.model.BotUser;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.BotUserRepository;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mensa bot to access mensa content
 */
@Component
@Slf4j
public class MensaBot extends TelegramLongPollingBot {

    @Autowired
    private TelegramConfig config;

    @Autowired
    private MensaConfig mensaConfig;

    @Autowired
    private KeyboardUtils keyboardUtils;

    @Autowired
    private BotUserRepository botUserRepo;

    @Autowired
    private MensaRepository mensaRepo;

    @Autowired
    private DishesMessageCreator dishesMessageCreator;

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private List<IDateSearchResultSupplier> dateSuppliers;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        } else {
            log.info("Unsupported update: {}", update.toString());
        }
    }

    private void handleCallbackQuery(CallbackQuery query) {
        String data = query.getData();
        if (query.getMessage() == null) {
            simpleCallbackAnswer(query.getId());
            log.info("Ignoring callback query because message is empty");
        } else {
            Long chatId = query.getMessage().getChatId();
            Integer messageId = query.getMessage().getMessageId();
            String queryId = query.getId();
            Optional<FilterInfo> filterInfo = FilterInfo.fromString(data);
            if (filterInfo.isPresent()) {
                handleFilterQuery(filterInfo.get(), messageId, chatId, queryId);
            } else {
                handleMensaChange(data, chatId, queryId);
            }
        }
    }

    private void handleFilterQuery(FilterInfo info, Integer messageId, Long chatId, String queryId) {
        simpleCallbackAnswer(queryId);
        EditMessageText editMessage = dishesMessageCreator.createEditMessage(info, messageId, chatId);
        try {
            editMessageText(editMessage);
        } catch (TelegramApiException e) {
            log.warn("Failed to edit message {}: {}", messageId, e.getMessage());
        }
    }

    private void simpleCallbackAnswer(String queryId) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(queryId);
        try {
            answerCallbackQuery(answer);
        } catch (TelegramApiException e) {
            log.warn("Failed to answer callback query with id {}", queryId);
        }
    }

    private void handleMensaChange(String mensaId, Long chatId, String queryId) {
        simpleCallbackAnswer(queryId);
        botUserRepo.save(BotUser.of(chatId,mensaId));
        sendResponse(chatId,createMainSelectMessage(mensaId));
    }

    private void handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = StringUtils.trimToEmpty(message.getText());
        Optional<BotUser> userOpt = botUserRepo.findByChatId(chatId);
        SendMessage result;
        if (StringUtils.equalsIgnoreCase(BotConstants.START_COMMAND, text) || StringUtils.equalsIgnoreCase(messagesService.getMessage(Messages.COMMAND_CHANGE_MENSA), text) || !userOpt.isPresent() || !userOpt.get().hasSelectedMensa()) {
            result = createSelectMensaMessage();
        } else if (StringUtils.equalsIgnoreCase(messagesService.getMessage(Messages.COMMAND_WEEKDAYS), text)) {
            result = createSelectDayOfWeekMessage(userOpt.get().getMensaId());
        } else if (StringUtils.equalsIgnoreCase(messagesService.getMessage(Messages.COMMAND_BACK), text)) {
            result = createMainSelectMessage(userOpt.get().getMensaId());
        } else if (message.hasLocation()) {
            Location location = message.getLocation();
            Optional<Mensa> nearestMensa = mensaRepo.findByPointNear(new Point(location.getLongitude(), location.getLatitude()), new Distance(10000, Metrics.KILOMETERS)).stream().findFirst();
            result = nearestMensa.map(this::createMainSelectMessage).orElseGet(this::createNoNearestMensa);
        } else {
            Mensa mensa = mensaRepo.findOne(userOpt.get().getMensaId());
            if (mensa == null) {
                result = createSelectMensaMessage();
            } else {
                Optional<DateSearchResult> resultOpt = dateSuppliers.stream().flatMap(supplier -> supplier.forTextAndMensaAsStream(text, mensa)).findFirst();
                if (resultOpt.isPresent()) {
                    result = dishesMessageCreator.createMessage(userOpt.get().getMensaId(), resultOpt.get());
                } else {
                    result = new SendMessage();
                    result.setText(messagesService.getMessage(Messages.RESPONSE_DONT_UNDERSTAND));
                    result.setReplyMarkup(keyboardUtils.createMainSelectKeyboard());
                }
            }
        }
        sendResponse(chatId, result);
    }

    private void sendResponse(Long chatId, SendMessage result) {
        try {
            log.info("Sending response to {}", chatId);
            result.setChatId(chatId);
            result.setParseMode("HTML");
            sendMessage(result);
        } catch (TelegramApiException e) {
            log.error("Failed to send response to {} due to error: {}", chatId, e.getMessage());
        }
    }

    private SendMessage createMainSelectMessage(String mensaId) {
        Mensa mensa = mensaRepo.findOne(mensaId);
        if (mensa == null) {
            return createSelectMensaMessage();
        } else {
            return createMainSelectMessage(mensa);
        }
    }

    private SendMessage createMainSelectMessage(Mensa mensa) {
        SendMessage result = new SendMessage();
        result.setText(messagesService.getMessage(Messages.RESPONSE_SELECT_DAY, mensa.getName()));
        result.setReplyMarkup(keyboardUtils.createMainSelectKeyboard());
        return result;
    }

    private SendMessage createNoNearestMensa() {
        SendMessage result = new SendMessage();
        result.setText(messagesService.getMessage(Messages.RESPONSE_NO_NEAREST_MENSA));
        return result;
    }


    private SendMessage createSelectDayOfWeekMessage(String mensaId) {
        SendMessage result = new SendMessage();
        result.setText(messagesService.getMessage(Messages.RESPONSE_SELECT_WEEKDAY));
        result.setReplyMarkup(keyboardUtils.createSelectDayOfWeekKeyboard(mensaConfig.isMensaOpenOnSaturday(mensaId)));
        return result;
    }



    private SendMessage createSelectMensaMessage() {
        SendMessage result = new SendMessage();
        result.setText(messagesService.getMessage(Messages.RESPONSE_SELECT_MENSA));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(mensaRepo.findAllByOrderByName().stream()
                .map(mensa -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(mensa.getName());
                    button.setCallbackData(mensa.getId());
                    return Arrays.asList(button);
                }).collect(Collectors.toList()));
        result.setReplyMarkup(markup);
        return result;
    }

    @Override
    public String getBotUsername() {
        return config.getBotname();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
