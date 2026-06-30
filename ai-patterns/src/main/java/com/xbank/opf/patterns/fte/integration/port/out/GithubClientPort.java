package com.xbank.opf.patterns.fte.integration.port.out;

import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;

public interface GithubClientPort {
    /**
     * Reads a PRD (Product Requirements Document) from a GitHub repository using the Agent's identity.
     */
    String readPrdFromRepo(AgentIdentityProfile agentIdentity, String repoOwner, String repoName, String filePath);
}
