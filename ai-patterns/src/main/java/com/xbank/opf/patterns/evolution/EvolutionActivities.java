package com.xbank.opf.patterns.evolution;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface EvolutionActivities {

    /**
     * Runs simulated security pipelines (like DLP checks) on the proposed skill.
     * @param sessionId The session identifier.
     * @param skillName The skill being evaluated.
     * @return A security report text.
     */
    @ActivityMethod
    String runSecurityChecks(String sessionId, String skillName);

    /**
     * Commits the actual skill level upgrade to the MongoDB Memory Bank.
     * @param sessionId The session identifier.
     * @param skillName The skill to update.
     * @param proposedLevel The new level to set.
     */
    @ActivityMethod
    void commitSkillUpgrade(String sessionId, String skillName, int proposedLevel);
}
