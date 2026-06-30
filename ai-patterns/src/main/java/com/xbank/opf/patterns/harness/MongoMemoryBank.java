package com.xbank.opf.patterns.harness;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MongoMemoryBank implements MemoryBank {

    private final MongoTemplate mongoTemplate;

    public MongoMemoryBank(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveMessage(String sessionId, AiMessage message) {
        Query query = new Query(Criteria.where("sessionId").is(sessionId));
        SessionMemory memory = mongoTemplate.findOne(query, SessionMemory.class);
        
        if (memory == null) {
            memory = new SessionMemory(sessionId);
        }
        
        memory.getMessages().add(message);
        mongoTemplate.save(memory);
    }

    @Override
    public List<AiMessage> getHistory(String sessionId) {
        Query query = new Query(Criteria.where("sessionId").is(sessionId));
        SessionMemory memory = mongoTemplate.findOne(query, SessionMemory.class);
        
        return memory != null ? memory.getMessages() : new ArrayList<>();
    }

    @Override
    public AgentFteData getFteData(String sessionId) {
        Query query = new Query(Criteria.where("sessionId").is(sessionId));
        SessionMemory memory = mongoTemplate.findOne(query, SessionMemory.class);
        
        return memory != null ? memory.getFteData() : new AgentFteData();
    }

    @Override
    public void updateFteData(String sessionId, AgentFteData updatedData) {
        Query query = new Query(Criteria.where("sessionId").is(sessionId));
        SessionMemory memory = mongoTemplate.findOne(query, SessionMemory.class);
        
        if (memory == null) {
            memory = new SessionMemory(sessionId);
        }
        
        memory.setFteData(updatedData);
        mongoTemplate.save(memory);
    }
}
