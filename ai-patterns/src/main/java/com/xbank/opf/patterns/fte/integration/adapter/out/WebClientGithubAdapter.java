package com.xbank.opf.patterns.fte.integration.adapter.out;

import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;
import com.xbank.opf.patterns.fte.integration.port.out.GithubClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientGithubAdapter implements GithubClientPort {

    private final WebClient webClient;

    public WebClientGithubAdapter(WebClient.Builder webClientBuilder, 
                                  @Value("${github.api.url:https://api.github.com}") String githubApiUrl) {
        this.webClient = webClientBuilder.baseUrl(githubApiUrl).build();
    }

    @Override
    public String readPrdFromRepo(AgentIdentityProfile agentIdentity, String repoOwner, String repoName, String filePath) {
        
        // Ensure Agent has appropriate RBAC role
        if (!agentIdentity.hasRole("ROLE_REPO_READ")) {
            throw new SecurityException("Agent " + agentIdentity.getEmployeeId() + " lacks ROLE_REPO_READ");
        }

        // Simulate fetching decoupled Vault token (using mock PAT in test)
        String mockVaultToken = "mock_pat_for_" + agentIdentity.getGithubUsername();

        try {
            return webClient.get()
                    .uri("/repos/{owner}/{repo}/contents/{path}", repoOwner, repoName, filePath)
                    .header("Authorization", "Bearer " + mockVaultToken)
                    .header("Accept", "application/vnd.github.v3.raw")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read PRD from GitHub: " + e.getMessage(), e);
        }
    }
}
