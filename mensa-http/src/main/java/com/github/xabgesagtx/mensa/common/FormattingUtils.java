package com.github.xabgesagtx.mensa.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormattingUtils {

	/**
	 * Converting number of bytes to human readable size.
	 *
	 * Found at https://stackoverflow.com/a/3758880
	 *
	 * @param bytes
	 * @param si
	 * @return
	 */
	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format(Locale.ENGLISH, "%.2f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
