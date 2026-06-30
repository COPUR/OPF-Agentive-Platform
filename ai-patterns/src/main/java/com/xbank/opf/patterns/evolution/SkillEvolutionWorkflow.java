package com.xbank.opf.patterns.evolution;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SkillEvolutionWorkflow {

    /**
     * Proposes a skill upgrade for the given session's AgentFteData.
     * @param sessionId The session ID mapping to the agent's memory.
     * @param skillName The name of the skill to upgrade.
     * @param proposedLevel The new proficiency level.
     * @return A status message describing the outcome.
     */
    @WorkflowMethod
    String proposeSkillUpgrade(String sessionId, String skillName, int proposedLevel);

    /**
     * Signal from a human admin to approve or reject the upgrade.
     * @param isApproved True if approved, false if rejected.
     */
    @SignalMethod
    void adminReviewDecision(boolean isApproved);
}
