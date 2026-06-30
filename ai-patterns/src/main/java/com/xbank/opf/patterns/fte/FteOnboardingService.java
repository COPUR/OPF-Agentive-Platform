package com.xbank.opf.patterns.fte;

import com.xbank.opf.patterns.fte.config.FteProperties;
import com.xbank.opf.patterns.harness.AgentFteData;
import com.xbank.opf.patterns.harness.MemoryBank;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FteOnboardingService implements ApplicationRunner {

    private final MemoryBank memoryBank;
    private final FteProperties properties;

    public FteOnboardingService(MemoryBank memoryBank, FteProperties properties) {
        this.memoryBank = memoryBank;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        onboardFteIfNotExists(AgentFteRole.AUTONOMOUS_INGESTION, "Agent 1: Autonomous Ingestion", "JIRA_PARSING", 3);
        onboardFteIfNotExists(AgentFteRole.TOPOLOGY_SYNTHESIS, "Agent 2: Low-Level Topology Synthesis", "CMDB_QUERYING", 3);
        onboardFteIfNotExists(AgentFteRole.COMPLIANCE_VALIDATION, "Agent 3: Compliance Validation & Remediation", "PGVECTOR_SEARCH", 3);
        onboardFteIfNotExists(AgentFteRole.STAKEHOLDER_SIGNOFF, "Agent 4: Stakeholder Sign-Off Facilitation", "OIDC_RBAC", 3);
        onboardFteIfNotExists(AgentFteRole.CODE_AUDIT_TELEMETRY, "Agent 5: Code Audit & SRE Chapter Telemetry", "SEMANTIC_TRIANGLE_CHECK", 3);
    }

    private void onboardFteIfNotExists(AgentFteRole role, String name, String defaultSkill, int skillLevel) {
        String sessionId = getSessionIdForRole(role);
        AgentFteData existingData = memoryBank.getFteData(sessionId);

        // Provision if missing or newly initialized
        if (existingData == null || existingData.getRole() == null) {
            AgentFteData newData = new AgentFteData(role, name);
            newData.updateSkill(defaultSkill, skillLevel);
            
            BigDecimal initialBudget = properties.getEconomics() != null && properties.getEconomics().getInitialBudget() != null 
                    ? properties.getEconomics().getInitialBudget() 
                    : new BigDecimal("100.00");
                    
            newData.getEconomicsProfile().setCurrentBudget(initialBudget);

            memoryBank.updateFteData(sessionId, newData);
            System.out.println("Onboarded FTE: " + name + " [" + sessionId + "] with " + initialBudget + " AI Credits");
        }
    }

    public static String getSessionIdForRole(AgentFteRole role) {
        return "fte-session-" + role.name().toLowerCase().replace("_", "-");
    }
}
