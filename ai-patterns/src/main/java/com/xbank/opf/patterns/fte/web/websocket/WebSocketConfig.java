package com.xbank.opf.patterns.fte.web.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final AgentFteWebSocketHandler agentFteWebSocketHandler;

    public WebSocketConfig(AgentFteWebSocketHandler agentFteWebSocketHandler) {
        this.agentFteWebSocketHandler = agentFteWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Expose the WebSocket endpoint.
        // In production, configure setAllowedOrigins properly.
        registry.addHandler(agentFteWebSocketHandler, "/ws/agent-fte")
                .setAllowedOrigins("*");
    }
}
