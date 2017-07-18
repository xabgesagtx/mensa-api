package com.github.xabgesagtx.mensa.persistence;

import com.github.xabgesagtx.mensa.model.Allergen;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for allergens
 */
public interface AllergenRepository extends MongoRepository<Allergen,Integer> {

    List<Allergen> findAllByOrderByNumberAsc();
}
