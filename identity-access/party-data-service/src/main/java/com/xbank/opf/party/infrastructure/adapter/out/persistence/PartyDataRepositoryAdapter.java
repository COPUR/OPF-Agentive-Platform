package com.xbank.opf.party.infrastructure.adapter.out.persistence;

import com.xbank.opf.party.domain.port.out.PartyDataPort;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class PartyDataRepositoryAdapter implements PartyDataPort {
    
    private final PartyDataMongoRepository mongoRepository;
    
    public PartyDataRepositoryAdapter(PartyDataMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }
    
    @Override
    public Optional<Object> findById(String id) {
        return mongoRepository.findById(id);
    }
}
