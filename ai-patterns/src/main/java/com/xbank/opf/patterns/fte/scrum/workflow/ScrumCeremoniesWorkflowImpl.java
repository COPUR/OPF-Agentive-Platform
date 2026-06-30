package com.xbank.opf.patterns.fte.scrum.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class ScrumCeremoniesWorkflowImpl implements ScrumCeremoniesWorkflow {

    private final ScrumCeremoniesActivities activities = Workflow.newActivityStub(
            ScrumCeremoniesActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(15))
                    .build()
    );

    private Boolean isApproved = null;

    @Override
    public String executeScrumCycle(String sprintContext) {
        
        // 1. Sprint Planning
        String planningResult = activities.runSprintPlanning(sprintContext);
        
        // 2. Daily Standup (simulated as part of the pipeline for now)
        String standupResult = activities.runDailyStandup(sprintContext);
        
        // 3. Sprint Review
        String reviewResult = activities.runSprintReview(sprintContext);
        
        // 4. Sprint Retrospective
        String retroResult = activities.runSprintRetrospective(sprintContext);
        
        // 5. Generate Comprehensive Report
        String finalReport = activities.generateScrumReport(planningResult, standupResult, reviewResult, retroResult);
        
        // Emitting the report so external systems can pick it up (e.g. log it or send to a UI)
        Workflow.getLogger(ScrumCeremoniesWorkflowImpl.class).info("Generated Scrum Report:\n" + finalReport);

        // 6. Block and wait for Human-In-The-Loop approval
        Workflow.await(() -> isApproved != null);

        if (isApproved) {
            return "SCRUM_CYCLE_APPROVED_BY_HITL";
        } else {
            return "SCRUM_CYCLE_REJECTED_BY_HITL";
        }
    }

    @Override
    public void approveScrumReport(boolean approved) {
        this.isApproved = approved;
    }
}
