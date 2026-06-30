package com.xbank.opf.patterns.fte.web.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.FteHarnessCoordinator;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AgentFteWebSocketHandler extends TextWebSocketHandler {

    private final FteHarnessCoordinator coordinator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Track active sessions
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public AgentFteWebSocketHandler(FteHarnessCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        activeSessions.put(session.getId(), session);
        System.out.println("WebSocket Connected: " + session.getId());
        
        // Send welcome message
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                "type", "SYSTEM",
                "message", "Connected to OpenFinance Agent-FTE Core."
        ))));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Map<String, String> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String userPrompt = payload.get("prompt");
            String roleStr = payload.getOrDefault("role", "ARCHITECTURE");

            AgentFteRole role = AgentFteRole.valueOf(roleStr.toUpperCase());
            
            // Dispatch asynchronously to not block the WebSocket thread
            Thread.startVirtualThread(() -> {
                try {
                    // Send typing indicator
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                            "type", "STATUS",
                            "message", "Agent " + role.name() + " is processing..."
                    ))));

                    // Delegate to the FteHarnessCoordinator
                    String response = coordinator.dispatchTask(role, userPrompt);

                    // Send response back
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                            "type", "AGENT_RESPONSE",
                            "role", role.name(),
                            "message", response
                    ))));
                } catch (Exception e) {
                    try {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                                "type", "ERROR",
                                "message", "Agent execution failed: " + e.getMessage()
                        ))));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                    "type", "ERROR",
                    "message", "Invalid payload format."
            ))));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        activeSessions.remove(session.getId());
        System.out.println("WebSocket Disconnected: " + session.getId());
    }
}
