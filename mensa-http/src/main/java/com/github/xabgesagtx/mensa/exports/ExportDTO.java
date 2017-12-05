package com.github.xabgesagtx.mensa.exports;

import com.github.xabgesagtx.mensa.common.FormattingUtils;
import lombok.Value;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Value
public class ExportDTO implements Comparable<ExportDTO> {
	private LocalDateTime dateTime;
	private long size;
	public String getSizeFormatted() {
		return FormattingUtils.humanReadableByteCount(size, false);
	}

	@Override
	public int compareTo(@NotNull ExportDTO o) {
		return new CompareToBuilder().append(o.dateTime,dateTime).toComparison();
	}
}
