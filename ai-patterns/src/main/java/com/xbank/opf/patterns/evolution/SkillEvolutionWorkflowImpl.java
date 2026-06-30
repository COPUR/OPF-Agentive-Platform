package com.xbank.opf.patterns.evolution;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class SkillEvolutionWorkflowImpl implements SkillEvolutionWorkflow {

    private boolean isReviewComplete = false;
    private boolean isApproved = false;

    private final EvolutionActivities activities = Workflow.newActivityStub(
            EvolutionActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(5))
                    .build()
    );

    @Override
    public String proposeSkillUpgrade(String sessionId, String skillName, int proposedLevel) {
        
        // 1. Run Security Pipeline
        String securityReport = activities.runSecurityChecks(sessionId, skillName);
        if (!securityReport.contains("PASSED")) {
            return "SKILL_UPGRADE_REJECTED_BY_SECURITY";
        }

        // 2. Wait for Human-in-the-Loop (HITL) approval
        // The workflow will block here durably until adminReviewDecision is signaled
        Workflow.await(() -> isReviewComplete);

        // 3. Evaluate human decision
        if (isApproved) {
            activities.commitSkillUpgrade(sessionId, skillName, proposedLevel);
            return "SKILL_UPGRADE_APPROVED_AND_COMMITTED";
        } else {
            return "SKILL_UPGRADE_REJECTED_BY_ADMIN";
        }
    }

    @Override
    public void adminReviewDecision(boolean isApproved) {
        this.isApproved = isApproved;
        this.isReviewComplete = true;
    }
}
