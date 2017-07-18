package com.github.xabgesagtx.mensa.persistence;

import com.github.xabgesagtx.mensa.model.Dish;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for dishes
 */
public interface DishRepository extends MongoRepository<Dish, String> {
	
	List<Dish> findByDateAndMensaId(LocalDate date, String mensaId);

	List<Dish> findByDateAndMensaIdOrderByIdAsc(LocalDate date, String mensaId);

	List<Dish> findByDateAndMensaIdAndLabelsInOrderByIdAsc(LocalDate date, String mensaId, List<String> labels);

	List<Dish> findByDateAndMensaIdAndLabelsNotInOrderByIdAsc(LocalDate date, String mensaId, List<String> labels);

	List<Dish> findAllOrderById();
}
