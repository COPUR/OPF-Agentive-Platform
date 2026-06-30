package com.xbank.opf.patterns.harness;

import java.time.Instant;

public class AiMessage {
    private String role;
    private String content;
    private Instant timestamp;

    public AiMessage() {}

    public AiMessage(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = Instant.now();
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
