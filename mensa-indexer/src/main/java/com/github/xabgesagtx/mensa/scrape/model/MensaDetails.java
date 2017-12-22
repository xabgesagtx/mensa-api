package com.github.xabgesagtx.mensa.scrape.model;

import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class MensaDetails {

	private String address;
	private String zipcode;
	private String city;

}
