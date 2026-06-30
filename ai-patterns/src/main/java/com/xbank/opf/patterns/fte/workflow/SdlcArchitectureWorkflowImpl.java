package com.xbank.opf.patterns.fte.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class SdlcArchitectureWorkflowImpl implements SdlcArchitectureWorkflow {

    private final SdlcArchitectureActivities activities = Workflow.newActivityStub(
            SdlcArchitectureActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(10))
                    .build()
    );

    @Override
    public String executeEndToEndArchitecture(String jiraPrdInput) {
        
        // 1. Ingestion Agent
        String context = activities.executeIngestion(jiraPrdInput);
        
        // 2. Topology Synthesis Agent
        String lld = activities.executeTopology(context);
        
        // 3. Compliance Agent
        String complianceResult = activities.executeCompliance(lld);
        
        return "SDLC E2E Complete. Compliance Result: " + complianceResult;
    }
}
