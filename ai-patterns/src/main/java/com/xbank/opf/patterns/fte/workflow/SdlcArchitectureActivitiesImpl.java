package com.xbank.opf.patterns.fte.workflow;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.FteHarnessCoordinator;
import org.springframework.stereotype.Component;

@Component
public class SdlcArchitectureActivitiesImpl implements SdlcArchitectureActivities {

    private final FteHarnessCoordinator coordinator;

    public SdlcArchitectureActivitiesImpl(FteHarnessCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public String executeIngestion(String jiraPrdInput) {
        System.out.println("Executing Ingestion Activity for input: " + jiraPrdInput);
        return coordinator.dispatchTask(AgentFteRole.AUTONOMOUS_INGESTION, "Process this PRD/Jira context: " + jiraPrdInput);
    }

    @Override
    public String executeTopology(String ingestedContext) {
        System.out.println("Executing Topology Activity for context: " + ingestedContext);
        return coordinator.dispatchTask(AgentFteRole.TOPOLOGY_SYNTHESIS, "Create LLD based on: " + ingestedContext);
    }

    @Override
    public String executeCompliance(String lldContext) {
        System.out.println("Executing Compliance Activity for LLD: " + lldContext);
        return coordinator.dispatchTask(AgentFteRole.COMPLIANCE_VALIDATION, "Validate this LLD against pgvector compliance rules: " + lldContext);
    }
}
