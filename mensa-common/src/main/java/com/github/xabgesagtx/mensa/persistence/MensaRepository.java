package com.github.xabgesagtx.mensa.persistence;

import com.github.xabgesagtx.mensa.model.Mensa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for labels
 */
public interface MensaRepository extends MongoRepository<Mensa, String> {

    List<Mensa> findAllByOrderByName();
}
