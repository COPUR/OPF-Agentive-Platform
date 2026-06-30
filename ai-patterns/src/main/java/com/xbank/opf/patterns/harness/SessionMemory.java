package com.xbank.opf.patterns.harness;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "agent_memories")
public class SessionMemory {
    @Id
    private String sessionId;
    private List<AiMessage> messages = new ArrayList<>();
    private AgentFteData fteData = new AgentFteData();

    public SessionMemory() {}

    public SessionMemory(String sessionId) {
        this.sessionId = sessionId;
        this.fteData = new AgentFteData();
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public List<AiMessage> getMessages() { return messages; }
    public void setMessages(List<AiMessage> messages) { this.messages = messages; }

    public AgentFteData getFteData() { return fteData; }
    public void setFteData(AgentFteData fteData) { this.fteData = fteData; }
}
