package com.xbank.opf.patterns.fte.integration;

import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;
import com.xbank.opf.patterns.fte.integration.adapter.out.WebClientGithubAdapter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WebClientGithubAdapterTest {

    private MockWebServer mockWebServer;
    private WebClientGithubAdapter githubAdapter;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        githubAdapter = new WebClientGithubAdapter(WebClient.builder(), baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testReadPrdFromRepo_Success() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("Mock PRD Content")
                .addHeader("Content-Type", "text/plain"));

        AgentIdentityProfile agent = new AgentIdentityProfile("agent_001", "Agent One", "agent_one_github", "agent_one_jira", "/vault/agent_one");
        agent.assignRole("ROLE_REPO_READ");

        String result = githubAdapter.readPrdFromRepo(agent, "xbank", "opf-docs", "prd.md");

        assertEquals("Mock PRD Content", result);

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/repos/xbank/opf-docs/contents/prd.md", request.getPath());
        assertEquals("Bearer mock_pat_for_agent_one_github", request.getHeader("Authorization"));
        assertEquals("application/vnd.github.v3.raw", request.getHeader("Accept"));
    }

    @Test
    void testReadPrdFromRepo_LacksRole() {
        AgentIdentityProfile agent = new AgentIdentityProfile("agent_001", "Agent One", "agent_one_github", "agent_one_jira", "/vault/agent_one");
        
        SecurityException exception = assertThrows(SecurityException.class, () -> 
            githubAdapter.readPrdFromRepo(agent, "xbank", "opf-docs", "prd.md")
        );
        assertTrue(exception.getMessage().contains("lacks ROLE_REPO_READ"));
    }

    @Test
    void testReadPrdFromRepo_HttpError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        AgentIdentityProfile agent = new AgentIdentityProfile("agent_001", "Agent One", "agent_one_github", "agent_one_jira", "/vault/agent_one");
        agent.assignRole("ROLE_REPO_READ");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            githubAdapter.readPrdFromRepo(agent, "xbank", "opf-docs", "invalid.md")
        );
        assertTrue(exception.getMessage().contains("Failed to read PRD from GitHub"));
    }
}
