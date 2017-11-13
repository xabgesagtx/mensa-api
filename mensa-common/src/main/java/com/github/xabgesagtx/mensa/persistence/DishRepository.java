package com.github.xabgesagtx.mensa.persistence;

import com.github.xabgesagtx.mensa.model.Dish;
import com.github.xabgesagtx.mensa.model.QDish;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for dishes
 */
public interface DishRepository extends MongoRepository<Dish, String>, QueryDslPredicateExecutor<Dish>, QuerydslBinderCustomizer<QDish> {
	
	List<Dish> findByDateAndMensaId(LocalDate date, String mensaId);

	List<Dish> findByDateAndMensaIdOrderByIdAsc(LocalDate date, String mensaId);

	List<Dish> findByDateAndMensaIdAndLabelsInOrderByIdAsc(LocalDate date, String mensaId, List<String> labels);

	List<Dish> findByDateAndMensaIdAndLabelsNotInOrderByIdAsc(LocalDate date, String mensaId, List<String> labels);

	List<Dish> findAllOrderById();

	@Override
	default void customize(QuerydslBindings bindings, QDish root) {
		bindings.bind(root.description).first((path, value) -> path.containsIgnoreCase(value));
		bindings.bind(root.to).first((path, value) -> root.date.before(value.plusDays(1)));
		bindings.bind(root.from).first((path, value) -> root.date.after(value.minusDays(1)));
	}
}
