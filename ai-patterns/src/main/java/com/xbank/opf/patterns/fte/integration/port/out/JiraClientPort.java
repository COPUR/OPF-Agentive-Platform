package com.xbank.opf.patterns.fte.integration.port.out;

import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;

public interface JiraClientPort {
    /**
     * Creates a Jira ticket on behalf of the Agent-FTE using their IAM credentials.
     */
    String createJiraTicket(AgentIdentityProfile agentIdentity, String projectKey, String summary, String description);
}
