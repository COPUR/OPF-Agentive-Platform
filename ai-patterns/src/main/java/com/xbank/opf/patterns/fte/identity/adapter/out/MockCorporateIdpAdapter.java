package com.xbank.opf.patterns.fte.identity.adapter.out;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;
import com.xbank.opf.patterns.fte.identity.port.out.AgentIdentityProviderPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MockCorporateIdpAdapter implements AgentIdentityProviderPort {

    private final Map<AgentFteRole, AgentIdentityProfile> mockIdpDatabase = new ConcurrentHashMap<>();

    public void saveIdentity(AgentFteRole role, AgentIdentityProfile profile) {
        mockIdpDatabase.put(role, profile);
    }

    @Override
    public Optional<AgentIdentityProfile> getIdentityForRole(AgentFteRole role) {
        return Optional.ofNullable(mockIdpDatabase.get(role));
    }
}
