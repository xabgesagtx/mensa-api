package com.github.xabgesagtx.mensa.persistence;

import com.github.xabgesagtx.mensa.model.Label;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for labels
 */
public interface LabelRepository extends MongoRepository<Label, String> {
	List<Label> findAllByOrderByName();
}
