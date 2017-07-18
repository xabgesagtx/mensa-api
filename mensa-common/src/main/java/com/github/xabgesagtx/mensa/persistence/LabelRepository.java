package com.github.xabgesagtx.mensa.persistence;

import com.github.xabgesagtx.mensa.model.Label;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for labels
 */
public interface LabelRepository extends MongoRepository<Label, String> {
}
