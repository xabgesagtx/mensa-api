package com.github.xabgesagtx.mensa.model;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model for an allergen
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
@EqualsAndHashCode
@ToString
@Document(collection = "allergen")
public class Allergen implements Comparable<Allergen> {

	@Id
	private String number;
	private String name;

	@Override
	public int compareTo(Allergen o) {
		return this.getNumber().compareTo(o.getNumber());
	}
}
