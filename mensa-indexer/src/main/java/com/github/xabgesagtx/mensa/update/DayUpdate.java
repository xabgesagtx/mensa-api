package com.github.xabgesagtx.mensa.update;

import com.github.xabgesagtx.mensa.model.Dish;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Class to hold all information for day to be updated, including the new dishes to be added
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class DayUpdate {
	
	private String mensaId;
	private LocalDate day;
	private List<Dish> dishes;

}
