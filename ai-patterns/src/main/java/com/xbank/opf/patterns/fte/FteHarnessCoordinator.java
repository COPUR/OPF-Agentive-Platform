package com.xbank.opf.patterns.fte;

import com.xbank.opf.patterns.evolution.SkillEvolutionWorkflow;
import com.xbank.opf.patterns.fte.config.FteProperties;
import com.xbank.opf.patterns.fte.economics.FteCostOptimizer;
import com.xbank.opf.patterns.fte.economics.FteTaskComplexity;
import com.xbank.opf.patterns.harness.AgentFteData;
import com.xbank.opf.patterns.harness.MemoryBank;
import com.xbank.opf.patterns.harness.ModelAgnosticHarness;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class FteHarnessCoordinator {

    private final ModelAgnosticHarness harness;
    private final FteCostOptimizer costOptimizer;
    private final FteProperties properties;
    private final MemoryBank memoryBank;
    private final WorkflowClient workflowClient;

    public FteHarnessCoordinator(ModelAgnosticHarness harness, FteCostOptimizer costOptimizer, FteProperties properties, MemoryBank memoryBank, WorkflowClient workflowClient) {
        this.harness = harness;
        this.costOptimizer = costOptimizer;
        this.properties = properties;
        this.memoryBank = memoryBank;
        this.workflowClient = workflowClient;
    }

    /**
     * Dispatches a task to a specific Agent-FTE, using their dedicated memory session.
     * @param role The Agent-FTE role to target.
     * @param taskPrompt The specific task or prompt for the agent.
     * @return The AI-generated response.
     */
    public String dispatchTask(AgentFteRole role, String taskPrompt) {
        String sessionId = FteOnboardingService.getSessionIdForRole(role);
        String systemPrompt = getSystemPromptForRole(role);
        
        try {
            // Ensure the FTE has enough budget to execute the task before proceeding
            costOptimizer.authorizeAndConsume(role, taskPrompt);
        } catch (com.xbank.opf.patterns.fte.economics.InsufficientFteBudgetException e) {
            System.err.println("Budget exceeded for " + role + ": " + e.getMessage());
            return "SYSTEM_ALERT: My AI token budget has been exhausted (" + e.getCurrentBudget() + " remaining, need " + e.getRequiredCost() + "). Please request a budget expansion from the HITL administrator to continue this task.";
        }

        System.out.println("Dispatching task to FTE: " + role.name() + " [Session: " + sessionId + "]");
        String response = harness.executeWithMemory(sessionId, systemPrompt, taskPrompt);
        
        evaluateSkillEvolution(role, sessionId, taskPrompt);
        
        return response;
    }

    private void evaluateSkillEvolution(AgentFteRole role, String sessionId, String taskPrompt) {
        FteTaskComplexity complexity = FteTaskComplexity.inferFromPrompt(taskPrompt);
        if (complexity == FteTaskComplexity.HIGH) {
            AgentFteData fteData = memoryBank.getFteData(sessionId);
            if (fteData != null && !fteData.getEmployeeSkills().isEmpty()) {
                // Find the primary/highest skill to upgrade
                Map.Entry<String, Integer> primarySkill = fteData.getEmployeeSkills().entrySet().iterator().next();
                String skillName = primarySkill.getKey();
                int currentLevel = primarySkill.getValue();
                
                System.out.println("Agent " + role.name() + " completed a HIGH complexity task. Autonomously proposing skill upgrade for " + skillName + " to level " + (currentLevel + 1));
                
                String workflowId = "skill-evolution-" + role.name().toLowerCase() + "-" + UUID.randomUUID().toString();
                SkillEvolutionWorkflow workflow = workflowClient.newWorkflowStub(
                        SkillEvolutionWorkflow.class,
                        WorkflowOptions.newBuilder()
                                .setWorkflowId(workflowId)
                                .setTaskQueue("EVOLUTION_TASK_QUEUE")
                                .build()
                );

                WorkflowClient.start(workflow::proposeSkillUpgrade, sessionId, skillName, currentLevel + 1);
            }
        }
    }

    private String getSystemPromptForRole(AgentFteRole role) {
        if (properties.getPrompts() != null && properties.getPrompts().containsKey(role)) {
            return properties.getPrompts().get(role);
        }
        return "You are an Agent-FTE for role: " + role.name() + ". Please execute your assigned vertical skills.";
    }
}
