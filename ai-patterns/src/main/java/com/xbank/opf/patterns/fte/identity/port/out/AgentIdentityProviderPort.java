package com.xbank.opf.patterns.fte.identity.port.out;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;

import java.util.Optional;

/**
 * Outbound Port to retrieve decoupled identity and credential metadata for an Agent-FTE.
 */
public interface AgentIdentityProviderPort {
    
    /**
     * Resolves the identity profile for the given Agent FTE role.
     * @param role The Agent FTE role.
     * @return The corporate identity profile.
     */
    Optional<AgentIdentityProfile> getIdentityForRole(AgentFteRole role);
}
