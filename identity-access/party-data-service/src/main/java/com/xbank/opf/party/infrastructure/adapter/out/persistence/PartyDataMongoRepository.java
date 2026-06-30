package com.xbank.opf.party.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Empty document interface to satisfy Spring Data MongoDB
 * In reality, this would map to a @Document("partyProfiles") class
 */
@Repository
public interface PartyDataMongoRepository extends MongoRepository<Object, String> {
    
    // List<Object> findByNationalId(String eid);

}
