package com.github.xabgesagtx.mensa.scrape.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Wrapper class to hold all relevant urls of a mensa
 */
@Getter
@AllArgsConstructor(staticName="of")
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class MenuUrls {
	
	private String today;
	private String tomorrow;
	private String thisWeek;
	private String nextWeek;

}
