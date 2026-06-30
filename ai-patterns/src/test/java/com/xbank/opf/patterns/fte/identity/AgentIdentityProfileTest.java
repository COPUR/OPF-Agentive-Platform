package com.xbank.opf.patterns.fte.identity;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.identity.adapter.in.AgentIdentitySeeder;
import com.xbank.opf.patterns.fte.identity.adapter.out.MockCorporateIdpAdapter;
import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;
import com.xbank.opf.patterns.harness.AgentFteData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AgentIdentityProfileTest {

    private MockCorporateIdpAdapter idpAdapter;
    private AgentIdentitySeeder seeder;

    @BeforeEach
    void setUp() {
        idpAdapter = new MockCorporateIdpAdapter();
        seeder = new AgentIdentitySeeder(idpAdapter);
        
        // Seed the IDP
        seeder.seedIdentities();
    }

    @Test
    void testIdentityResolvedForArchitectureAgent() {
        Optional<AgentIdentityProfile> profileOpt = idpAdapter.getIdentityForRole(AgentFteRole.TOPOLOGY_SYNTHESIS);
        
        assertTrue(profileOpt.isPresent());
        AgentIdentityProfile profile = profileOpt.get();
        
        assertEquals("agent.architecture@xbank.ae", profile.getCorporateEmail());
        assertEquals("FTE-AI-1002", profile.getEmployeeId());
        assertEquals("opf-agent-arch", profile.getGithubUsername());
        
        // Check RBAC roles
        assertTrue(profile.hasRole("ROLE_PRD_READ"));
        assertTrue(profile.hasRole("ROLE_ARCH_WRITE"));
        assertFalse(profile.hasRole("ROLE_CBUAE_DOCS_READ")); // Compliance role
    }

    @Test
    void testIdentityIntegrationWithAgentData() {
        AgentFteData data = new AgentFteData(AgentFteRole.COMPLIANCE_VALIDATION, "Compliance Agent");
        
        Optional<AgentIdentityProfile> profileOpt = idpAdapter.getIdentityForRole(data.getRole());
        assertTrue(profileOpt.isPresent());
        
        // Decoupled injection
        data.setIdentityProfile(profileOpt.get());
        
        assertEquals("FTE-AI-1003", data.getIdentityProfile().getEmployeeId());
        assertEquals("aws/secretsmanager/xbank/agents/compliance/credentials", data.getIdentityProfile().getCredentialVaultPath());
        assertTrue(data.getIdentityProfile().hasRole("ROLE_AUDIT_LOG_WRITE"));
    }
}
