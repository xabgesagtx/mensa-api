package com.github.xabgesagtx.mensa.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Model for a single dish
 */
@Getter
@AllArgsConstructor(staticName="of")
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@Document(collection = "dish")
public class Dish {

	private String id;
	private LocalDate date;
	private String mensaId;
	private String category;
	private String description;
	private List<String> labels;
	private List<BigDecimal> prices;
	private List<Integer> allergens;

}
