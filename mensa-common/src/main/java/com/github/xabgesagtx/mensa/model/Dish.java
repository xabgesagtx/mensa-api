package com.github.xabgesagtx.mensa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Model for a single dish
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "dish")
public class Dish {

	private String id;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate date;
	private String mensaId;
	private String category;
	private String description;
	private List<String> labels;
	private List<BigDecimal> prices;
	private List<Integer> allergens;

	@Transient
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate from;
	@Transient
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate to;

	public static Dish of(String id, LocalDate date, String mensaId, String category, String description, List<String> labels, List<BigDecimal> prices, List<Integer> allergens) {
		return Dish.builder().id(id).date(date).mensaId(mensaId).category(category).description(description).labels(labels).prices(prices).allergens(allergens).build();
	}

}
