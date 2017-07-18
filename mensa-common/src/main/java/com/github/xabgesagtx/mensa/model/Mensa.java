package com.github.xabgesagtx.mensa.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Model for a mensa
 */
@Getter
@Setter
@AllArgsConstructor(staticName="of")
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "mensa")
public class Mensa {
	
	@Id
	private String id;
	private String name;
	private String mainUrl;
	private String todayUrl;
	private String tomorrowUrl;
	private String thisWeekUrl;
	private String nextWeekUrl;
	private LocalDateTime updatedAt;

}
