package com.xbank.opf.patterns.fte.web.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.FteHarnessCoordinator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentFteWebSocketHandlerTest {

    private AgentFteWebSocketHandler handler;
    private DummyWebSocketSession dummySession;
    private ObjectMapper objectMapper;

    static class DummyFteHarnessCoordinator extends FteHarnessCoordinator {
        public DummyFteHarnessCoordinator() {
            super(null, null, null, null, null);
        }

        @Override
        public String dispatchTask(AgentFteRole role, String taskPrompt) {
            if ("FAIL_TASK".equals(taskPrompt)) {
                throw new RuntimeException("Simulated failure");
            }
            return "Simulated AI Response for " + role.name();
        }
    }

    static class DummyWebSocketSession implements WebSocketSession {
        public List<String> messagesSent = java.util.Collections.synchronizedList(new ArrayList<>());
        private String id = "test-session-123";
        private boolean isOpen = true;
        public CountDownLatch latch = new CountDownLatch(1);

        @Override public String getId() { return id; }
        @Override public URI getUri() { return null; }
        @Override public HttpHeaders getHandshakeHeaders() { return null; }
        @Override public Map<String, Object> getAttributes() { return null; }
        @Override public Principal getPrincipal() { return null; }
        @Override public InetSocketAddress getLocalAddress() { return null; }
        @Override public InetSocketAddress getRemoteAddress() { return null; }
        @Override public String getAcceptedProtocol() { return null; }
        @Override public void setTextMessageSizeLimit(int messageSizeLimit) {}
        @Override public int getTextMessageSizeLimit() { return 0; }
        @Override public void setBinaryMessageSizeLimit(int messageSizeLimit) {}
        @Override public int getBinaryMessageSizeLimit() { return 0; }
        @Override public List<org.springframework.web.socket.WebSocketExtension> getExtensions() { return null; }
        
        @Override
        public void sendMessage(WebSocketMessage<?> message) throws IOException {
            messagesSent.add(((TextMessage) message).getPayload());
            latch.countDown();
        }

        @Override public boolean isOpen() { return isOpen; }
        @Override public void close() throws IOException { isOpen = false; }
        @Override public void close(CloseStatus status) throws IOException { isOpen = false; }
    }

    @BeforeEach
    void setUp() {
        handler = new AgentFteWebSocketHandler(new DummyFteHarnessCoordinator());
        dummySession = new DummyWebSocketSession();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testConnectionEstablished() throws Exception {
        handler.afterConnectionEstablished(dummySession);
        
        assertEquals(1, dummySession.messagesSent.size());
        Map<String, String> response = objectMapper.readValue(dummySession.messagesSent.get(0), Map.class);
        assertEquals("SYSTEM", response.get("type"));
        assertEquals("Connected to OpenFinance Agent-FTE Core.", response.get("message"));
    }

    @Test
    void testHandleTextMessage_Success() throws Exception {
        handler.afterConnectionEstablished(dummySession);
        dummySession.messagesSent.clear();
        dummySession.latch = new CountDownLatch(2);

        // Simulate incoming message
        Map<String, String> payload = Map.of(
                "prompt", "Analyze architecture",
                "role", "TOPOLOGY_SYNTHESIS"
        );
        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(payload));
        
        handler.handleTextMessage(dummySession, textMessage);

        dummySession.latch.await(3, TimeUnit.SECONDS);

        assertEquals(2, dummySession.messagesSent.size());
        
        // Check typing status
        Map<String, String> statusMsg = objectMapper.readValue(dummySession.messagesSent.get(0), Map.class);
        assertEquals("STATUS", statusMsg.get("type"));
        
        // Check actual response
        Map<String, String> responseMsg = objectMapper.readValue(dummySession.messagesSent.get(1), Map.class);
        assertEquals("AGENT_RESPONSE", responseMsg.get("type"));
        assertEquals("TOPOLOGY_SYNTHESIS", responseMsg.get("role"));
        assertEquals("Simulated AI Response for TOPOLOGY_SYNTHESIS", responseMsg.get("message"));
    }

    @Test
    void testHandleTextMessage_Exception() throws Exception {
        handler.afterConnectionEstablished(dummySession);
        dummySession.messagesSent.clear();
        dummySession.latch = new CountDownLatch(2);

        Map<String, String> payload = Map.of(
                "prompt", "FAIL_TASK",
                "role", "COMPLIANCE_VALIDATION"
        );
        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(payload));
        
        handler.handleTextMessage(dummySession, textMessage);

        dummySession.latch.await(3, TimeUnit.SECONDS);

        assertEquals(2, dummySession.messagesSent.size());
        
        Map<String, String> errorMsg = objectMapper.readValue(dummySession.messagesSent.get(1), Map.class);
        assertEquals("ERROR", errorMsg.get("type"));
        assertTrue(errorMsg.get("message").contains("Simulated failure"));
    }
}
