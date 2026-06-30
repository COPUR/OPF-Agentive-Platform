package com.xbank.opf.patterns.fte.integration.adapter.out;

import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;
import com.xbank.opf.patterns.fte.integration.port.out.JiraClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class WebClientJiraAdapter implements JiraClientPort {

    private final WebClient webClient;

    public WebClientJiraAdapter(WebClient.Builder webClientBuilder, 
                                @Value("${jira.api.url:https://xbank.atlassian.net/rest/api/3}") String jiraApiUrl) {
        this.webClient = webClientBuilder.baseUrl(jiraApiUrl).build();
    }

    @Override
    public String createJiraTicket(AgentIdentityProfile agentIdentity, String projectKey, String summary, String description) {
        
        // Ensure Agent has appropriate RBAC role
        if (!agentIdentity.hasRole("ROLE_JIRA_CREATE")) {
            throw new SecurityException("Agent " + agentIdentity.getEmployeeId() + " lacks ROLE_JIRA_CREATE");
        }

        // Simulate fetching decoupled Vault token (using mock API Key in test)
        String mockVaultToken = "mock_jira_key_for_" + agentIdentity.getJiraAccountId();

        Map<String, Object> payload = Map.of(
            "fields", Map.of(
                "project", Map.of("key", projectKey),
                "summary", summary,
                "description", Map.of(
                    "type", "doc",
                    "version", 1,
                    "content", new Object[]{
                        Map.of("type", "paragraph", "content", new Object[]{
                            Map.of("type", "text", "text", description)
                        })
                    }
                ),
                "issuetype", Map.of("name", "Task")
            )
        );

        try {
            return webClient.post()
                    .uri("/issue")
                    .header("Authorization", "Bearer " + mockVaultToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Jira ticket: " + e.getMessage(), e);
        }
    }
}
