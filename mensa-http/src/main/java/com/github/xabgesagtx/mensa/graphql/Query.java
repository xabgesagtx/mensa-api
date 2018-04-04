package com.github.xabgesagtx.mensa.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.github.xabgesagtx.mensa.model.Mensa;
import com.github.xabgesagtx.mensa.persistence.MensaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Root resolver for graphql query
 */
@Component
public class Query implements GraphQLQueryResolver {

    @Autowired
    private MensaRepository repo;

    public Mensa mensa(String id) {
        return repo.findById(id).orElse(null);
    }

}
