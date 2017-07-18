package com.github.xabgesagtx.mensa.bot.messages;

import com.github.xabgesagtx.mensa.config.BotConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

/**
 *
 * Service to get messages for the default locale
 *
 */
@Service
public class MessagesService {

    @Autowired
    private MessageSource messages;

    /**
     * Get message for default locale
     * @param key of the message to get
     * @param params of the message
     * @return the full message with params included
     */
    public String getMessage(String key, Object... params) throws NoSuchMessageException {
        return messages.getMessage(key, params, BotConstants.DEFAULT_LOCALE);
    }


}
