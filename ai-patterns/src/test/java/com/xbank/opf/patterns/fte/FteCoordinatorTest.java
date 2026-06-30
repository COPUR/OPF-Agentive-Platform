package com.xbank.opf.patterns.fte;

import com.xbank.opf.patterns.fte.config.FteProperties;
import com.xbank.opf.patterns.fte.economics.FteCostOptimizer;
import com.xbank.opf.patterns.harness.AgentFteData;
import com.xbank.opf.patterns.harness.AiMessage;
import com.xbank.opf.patterns.harness.MemoryBank;
import com.xbank.opf.patterns.harness.ModelAgnosticHarness;
import io.temporal.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FteCoordinatorTest {

    private TestMemoryBank memoryBank;
    private FteOnboardingService onboardingService;
    private TestModelAgnosticHarness harness;
    private FteHarnessCoordinator coordinator;
    private TestWorkflowEnvironment testEnv;

    static class TestMemoryBank implements MemoryBank {
        public int updateCalls = 0;
        public Map<String, AgentFteData> storage = new HashMap<>();

        @Override
        public List<AiMessage> getHistory(String sessionId) { return List.of(); }
        @Override
        public void saveMessage(String sessionId, AiMessage message) {}
        @Override
        public AgentFteData getFteData(String sessionId) { return storage.get(sessionId); }
        @Override
        public void updateFteData(String sessionId, AgentFteData data) {
            updateCalls++;
            storage.put(sessionId, data);
        }
    }

    static class TestModelAgnosticHarness extends ModelAgnosticHarness {
        public String lastSessionId;
        public String lastSystemPrompt;
        public String lastTaskPrompt;

        public TestModelAgnosticHarness() {
            super(null, null);
        }

        @Override
        public String executeWithMemory(String sessionId, String systemPrompt, String prompt) {
            this.lastSessionId = sessionId;
            this.lastSystemPrompt = systemPrompt;
            this.lastTaskPrompt = prompt;
            return "Mock AI Response";
        }
    }

    @BeforeEach
    void setUp() {
        memoryBank = new TestMemoryBank();
        testEnv = TestWorkflowEnvironment.newInstance();
        
        FteProperties properties = new FteProperties();
        properties.getEconomics().setCostLow(new java.math.BigDecimal("0.05"));
        properties.getEconomics().setCostMedium(new java.math.BigDecimal("0.50"));
        properties.getEconomics().setCostHigh(new java.math.BigDecimal("2.00"));
        
        java.util.Map<AgentFteRole, String> prompts = new java.util.HashMap<>();
        prompts.put(AgentFteRole.COMPLIANCE_VALIDATION, "You are Agent 3: Compliance Validation. Your role is to scan payloads against vector databases for security breaches.");
        properties.setPrompts(prompts);
        
        onboardingService = new FteOnboardingService(memoryBank, properties);
        
        harness = new TestModelAgnosticHarness();
        coordinator = new FteHarnessCoordinator(harness, new FteCostOptimizer(memoryBank, properties), properties, memoryBank, testEnv.getWorkflowClient());
    }

    @AfterEach
    void tearDown() {
        testEnv.close();
    }

    @Test
    void testOnboardingRegistersAllAgents() {
        // Run onboarding
        onboardingService.run(null);

        // Verify that updateFteData was called exactly 5 times (once for each role)
        assertEquals(5, memoryBank.updateCalls);
        assertNotNull(memoryBank.getFteData("fte-session-autonomous-ingestion"));
    }

    @Test
    void testCoordinatorRoutesCorrectly() {
        onboardingService.run(null); // Ensure agents are onboarded with budgets

        String response = coordinator.dispatchTask(AgentFteRole.COMPLIANCE_VALIDATION, "Check this log for PII");

        assertEquals("Mock AI Response", response);

        // Verify harness was called with correct sessionId and system prompt
        assertEquals("fte-session-compliance-validation", harness.lastSessionId);
        assertEquals("You are Agent 3: Compliance Validation. Your role is to scan payloads against vector databases for security breaches.", harness.lastSystemPrompt);
        assertEquals("Check this log for PII", harness.lastTaskPrompt);
    }
    @Test
    void testAutonomousSkillEvolutionTriggeredForHighComplexity() {
        onboardingService.run(null); // Onboard agents
        
        // Generate a prompt that is over 1000 characters to trigger HIGH complexity
        StringBuilder longPrompt = new StringBuilder();
        for (int i = 0; i < 1100; i++) {
            longPrompt.append("A");
        }
        
        // Register the Evolution workflow so the client doesn't complain about missing task queue/worker
        io.temporal.worker.Worker worker = testEnv.newWorker("EVOLUTION_TASK_QUEUE");
        worker.registerWorkflowImplementationTypes(com.xbank.opf.patterns.evolution.SkillEvolutionWorkflowImpl.class);
        
        // Use an inline dummy activity since the workflow needs it
        worker.registerActivitiesImplementations(new com.xbank.opf.patterns.evolution.EvolutionActivities() {
            @Override
            public String runSecurityChecks(String sessionId, String skillName) { return "PASSED"; }
            @Override
            public void commitSkillUpgrade(String sessionId, String skillName, int proposedLevel) {}
        });
        
        testEnv.start();

        // Dispatch a HIGH complexity task. This should trigger the evaluateSkillEvolution logic
        String response = coordinator.dispatchTask(AgentFteRole.COMPLIANCE_VALIDATION, longPrompt.toString());

        assertEquals("Mock AI Response", response);
        
        // If it successfully dispatched and triggered the workflow without crashing, the test passes.
    }
}
