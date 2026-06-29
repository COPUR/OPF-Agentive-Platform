package com.xbank.opf.party.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Empty document interface to satisfy Spring Data MongoDB
 * In reality, this would map to a @Document("partyProfiles") class
 */
@Repository
public interface PartyDataRepository extends MongoRepository<Object, String> {
    
    // List<Object> findByNationalId(String eid);

}
