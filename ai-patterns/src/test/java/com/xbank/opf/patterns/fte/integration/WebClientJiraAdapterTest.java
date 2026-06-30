package com.xbank.opf.patterns.fte.integration;

import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;
import com.xbank.opf.patterns.fte.integration.adapter.out.WebClientJiraAdapter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WebClientJiraAdapterTest {

    private MockWebServer mockWebServer;
    private WebClientJiraAdapter jiraAdapter;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        jiraAdapter = new WebClientJiraAdapter(WebClient.builder(), baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testCreateJiraTicket_Success() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":\"10000\",\"key\":\"XB-1\"}")
                .addHeader("Content-Type", "application/json"));

        AgentIdentityProfile agent = new AgentIdentityProfile("agent_002", "Agent Two", "agent_two_github", "agent_two_jira", "/vault/agent_two");
        agent.assignRole("ROLE_JIRA_CREATE");

        String result = jiraAdapter.createJiraTicket(agent, "XB", "Review PRD", "Need to review PRD for security");

        assertTrue(result.contains("XB-1"));

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/issue", request.getPath());
        assertEquals("Bearer mock_jira_key_for_agent_two_jira", request.getHeader("Authorization"));
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("\"project\":{\"key\":\"XB\"}"));
        assertTrue(body.contains("Need to review PRD for security"));
    }

    @Test
    void testCreateJiraTicket_LacksRole() {
        AgentIdentityProfile agent = new AgentIdentityProfile("agent_002", "Agent Two", "agent_two_github", "agent_two_jira", "/vault/agent_two");
        
        SecurityException exception = assertThrows(SecurityException.class, () -> 
            jiraAdapter.createJiraTicket(agent, "XB", "Review PRD", "Need to review PRD")
        );
        assertTrue(exception.getMessage().contains("lacks ROLE_JIRA_CREATE"));
    }

    @Test
    void testCreateJiraTicket_HttpError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        AgentIdentityProfile agent = new AgentIdentityProfile("agent_002", "Agent Two", "agent_two_github", "agent_two_jira", "/vault/agent_two");
        agent.assignRole("ROLE_JIRA_CREATE");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            jiraAdapter.createJiraTicket(agent, "XB", "Review PRD", "Need to review PRD")
        );
        assertTrue(exception.getMessage().contains("Failed to create Jira ticket"));
    }
}
