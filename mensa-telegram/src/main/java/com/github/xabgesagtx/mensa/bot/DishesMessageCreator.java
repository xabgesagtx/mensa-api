package com.github.xabgesagtx.mensa.bot;

import com.github.xabgesagtx.mensa.bot.date.DateSearchResult;
import com.github.xabgesagtx.mensa.bot.filter.FilterInfo;
import com.github.xabgesagtx.mensa.bot.filter.FilterType;
import com.github.xabgesagtx.mensa.bot.messages.MessagesService;
import com.github.xabgesagtx.mensa.bot.model.DishBotDTO;
import com.github.xabgesagtx.mensa.config.BotConstants;
import com.github.xabgesagtx.mensa.config.FilterConfig;
import com.github.xabgesagtx.mensa.config.TelegramConfig;
import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.DishRepository;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Component to create a message that displays dishes
 */
@Component
@Slf4j
public class DishesMessageCreator {

    private static final DateTimeFormatter TEMPLATE_DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy");

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private DishRepository dishRepo;

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private TelegramConfig telegramConfig;

    @Autowired
    private MensaRepository mensaRepo;

    @Autowired
    private KeyboardUtils keyboardUtils;

    /**
     * Create a new message displaying dishes for a day
     * @param mensaId of the mensa to create the message for
     * @param dateSearchResult result of the date search
     * @return the message to send to the user
     */
    public SendMessage createMessage(String mensaId, DateSearchResult dateSearchResult) {
        SendMessage message = new SendMessage();
        List<DishBotDTO> dishes = getDishBotDTOs(mensaId, dateSearchResult.getDateToUse());
        message.setText(formatText(mensaId, dateSearchResult, dishes));
        message.setReplyMarkup(keyboardUtils.createFilterKeyboard(mensaId, dateSearchResult.getDateToUse(), BotConstants.FILTER_ALL_VALUE));
        return message;
    }

    public EditMessageText createEditMessage(FilterInfo filterInfo, Integer messageId, Long chatId) {
        EditMessageText message = new EditMessageText();
        List<DishBotDTO> dishes = getDishBotDTOs(filterInfo);
        message.setText(formatText(filterInfo.getMensaId(), DateSearchResult.of(filterInfo.getDate(), Optional.empty()), dishes));
        message.setReplyMarkup(keyboardUtils.createFilterKeyboard(filterInfo.getMensaId(), filterInfo.getDate(), filterInfo.getValue()));
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setParseMode("HTML");
        return message;
    }

    private String formatText(String mensaId, DateSearchResult dateSearchResult, List<DishBotDTO> dishes) {
        String result;
        try {
            Mensa mensa = mensaRepo.findById(mensaId).orElse(null);
            Map<String,Object> root = new HashMap<>();
            root.put("mensa", mensa);
            if (dateSearchResult.getAlternativeDate().isPresent()) {
                root.put("originalDate", TEMPLATE_DATE_FORMAT.format(dateSearchResult.getDate()));
            }
            root.put("date", TEMPLATE_DATE_FORMAT.format(dateSearchResult.getDateToUse()));
            root.put("dishes", dishes);
            root.put("messagesService", messagesService);
            Template template = freemarkerConfig.getTemplate("dishes.ftl");
            StringWriter out = new StringWriter();
            template.process(root, out);
            result = out.toString();
        } catch (IOException e) {
            log.error("Failed to load template: {}", e.getMessage());
            result = "Internal Server Error :)";
        } catch (TemplateException e) {
            log.error("Failed to process template for {} and day {}: {}", mensaId, dateSearchResult.getDateToUse(), e.getMessage());
            result = "Internal Server Error :)";
        }
        return result;
    }

    private List<DishBotDTO> getDishBotDTOs(FilterInfo filterInfo) {
        Optional<FilterConfig> filterConfigOpt = telegramConfig.getFilters().stream().filter(filter -> StringUtils.equals(filter.getValue(), filterInfo.getValue())).findFirst();
        if (filterConfigOpt.isPresent()) {
            FilterConfig filterConfig = filterConfigOpt.get();
            if (filterConfig.getType() == FilterType.NEGATIVE) {
                return convert(dishRepo.findByDateAndMensaIdAndLabelsNotInOrderByIdAsc(filterInfo.getDate(), filterInfo.getMensaId(), filterConfig.getLabelsToFilter()));
            } else {
                return convert(dishRepo.findByDateAndMensaIdAndLabelsInOrderByIdAsc(filterInfo.getDate(), filterInfo.getMensaId(), filterConfig.getLabelsToFilter()));
            }
        } else {
            return getDishBotDTOs(filterInfo.getMensaId(), filterInfo.getDate());
        }
    }

    private List<DishBotDTO> getDishBotDTOs(String mensaId, LocalDate date) {
        return convert(dishRepo.findByDateAndMensaId(date, mensaId));
    }

    private List<DishBotDTO> convert(List<Dish> dishes) {
        return dishes.stream().map(DishBotDTO::fromDish).collect(Collectors.toList());
    }


}
