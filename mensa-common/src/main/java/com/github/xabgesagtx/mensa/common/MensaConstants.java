package com.github.xabgesagtx.mensa.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MensaConstants {

	public static final String EXPORT_DATE_FORMAT = "yyyyMMddHHmm";
	public static final DateTimeFormatter EXPORT_DATE_FORMATTER = DateTimeFormatter.ofPattern(EXPORT_DATE_FORMAT);

}
