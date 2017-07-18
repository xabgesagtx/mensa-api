package com.github.xabgesagtx.mensa.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;

/**
 * Constants used in the bot
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotConstants {

    public static final String FILTER_ALL_VALUE = "al";
    public static final Locale DEFAULT_LOCALE =Locale.GERMAN;

    public static final String START_COMMAND = "/start";
}
