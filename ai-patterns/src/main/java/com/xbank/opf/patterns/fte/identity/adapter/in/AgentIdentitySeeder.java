package com.xbank.opf.patterns.fte.identity.adapter.in;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.identity.adapter.out.MockCorporateIdpAdapter;
import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AgentIdentitySeeder {

    private final MockCorporateIdpAdapter idpAdapter;

    public AgentIdentitySeeder(MockCorporateIdpAdapter idpAdapter) {
        this.idpAdapter = idpAdapter;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedIdentities() {
        // Seed AUTONOMOUS_INGESTION Agent
        idpAdapter.saveIdentity(AgentFteRole.AUTONOMOUS_INGESTION, new AgentIdentityProfile(
                "agent.ingestion@xbank.ae",
                "FTE-AI-1001",
                "opf-agent-ingest",
                "5f9d1a-ingest-8409",
                "aws/secretsmanager/xbank/agents/ingestion/credentials",
                Set.of("ROLE_PRD_READ", "ROLE_JIRA_CREATE", "ROLE_S3_READ")
        ));

        // Seed ARCHITECTURE Agent (TOPOLOGY_SYNTHESIS)
        idpAdapter.saveIdentity(AgentFteRole.TOPOLOGY_SYNTHESIS, new AgentIdentityProfile(
                "agent.architecture@xbank.ae",
                "FTE-AI-1002",
                "opf-agent-arch",
                "5f9d1a-arch-8410",
                "aws/secretsmanager/xbank/agents/architecture/credentials",
                Set.of("ROLE_PRD_READ", "ROLE_REPO_READ", "ROLE_ARCH_WRITE")
        ));

        // Seed COMPLIANCE_VALIDATION Agent
        idpAdapter.saveIdentity(AgentFteRole.COMPLIANCE_VALIDATION, new AgentIdentityProfile(
                "agent.compliance@xbank.ae",
                "FTE-AI-1003",
                "opf-agent-comp",
                "5f9d1a-comp-8411",
                "aws/secretsmanager/xbank/agents/compliance/credentials",
                Set.of("ROLE_PRD_READ", "ROLE_CBUAE_DOCS_READ", "ROLE_AUDIT_LOG_WRITE")
        ));

        // Seed STAKEHOLDER_SIGNOFF Agent
        idpAdapter.saveIdentity(AgentFteRole.STAKEHOLDER_SIGNOFF, new AgentIdentityProfile(
                "agent.stakeholder@xbank.ae",
                "FTE-AI-1004",
                "opf-agent-stake",
                "5f9d1a-stake-8412",
                "aws/secretsmanager/xbank/agents/stakeholder/credentials",
                Set.of("ROLE_JIRA_READ", "ROLE_JIRA_COMMENT", "ROLE_EMAIL_SEND")
        ));

        // Seed CODE_AUDIT_TELEMETRY Agent
        idpAdapter.saveIdentity(AgentFteRole.CODE_AUDIT_TELEMETRY, new AgentIdentityProfile(
                "agent.audit@xbank.ae",
                "FTE-AI-1005",
                "opf-agent-audit",
                "5f9d1a-audit-8413",
                "aws/secretsmanager/xbank/agents/audit/credentials",
                Set.of("ROLE_REPO_READ", "ROLE_SONAR_READ", "ROLE_DATADOG_READ")
        ));

        System.out.println("[AgentIdentitySeeder] Loaded decoupled corporate identities for all Agent-FTEs into IDP Mock.");
    }
}
