package com.github.xabgesagtx.mensa.persistence;

import com.github.xabgesagtx.mensa.model.BotUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository for the users of the bot
 */
public interface BotUserRepository extends MongoRepository<BotUser, Long> {

    Optional<BotUser> findByChatId(Long id);

}
