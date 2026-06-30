package com.xbank.opf.patterns.evolution;

import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SkillEvolutionWorkflowImplTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private SkillEvolutionWorkflow workflow;
    private TestEvolutionActivities activitiesImpl;

    public static class TestEvolutionActivities implements EvolutionActivities {
        public boolean commitCalled = false;
        public String securityResult = "PASSED";

        @Override
        public String runSecurityChecks(String sessionId, String skillName) {
            return securityResult;
        }

        @Override
        public void commitSkillUpgrade(String sessionId, String skillName, int proposedLevel) {
            commitCalled = true;
        }
    }

    @BeforeEach
    void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker("EVOLUTION_TASK_QUEUE");
        
        worker.registerWorkflowImplementationTypes(SkillEvolutionWorkflowImpl.class);

        activitiesImpl = new TestEvolutionActivities();
        worker.registerActivitiesImplementations(activitiesImpl);

        testEnv.start();

        workflow = testEnv.getWorkflowClient().newWorkflowStub(
                SkillEvolutionWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue("EVOLUTION_TASK_QUEUE").build()
        );
    }

    @AfterEach
    void tearDown() {
        testEnv.close();
    }

    @Test
    void testSkillUpgradeApproved() {
        activitiesImpl.securityResult = "PASSED";

        // Start workflow asynchronously
        io.temporal.client.WorkflowClient.start(workflow::proposeSkillUpgrade, "session-1", "API_INTEGRATION", 2);

        // Signal human approval
        workflow.adminReviewDecision(true);

        // Fetch result (blocks until workflow completes)
        // We have to get an untyped stub or use a future to get the result. 
        // Wait, the easiest way is to use WorkflowStub.fromTyped(workflow).getResult(String.class);
        String result = io.temporal.client.WorkflowStub.fromTyped(workflow).getResult(String.class);
        assertEquals("SKILL_UPGRADE_APPROVED_AND_COMMITTED", result);

        // Verify commit was called
        assertTrue(activitiesImpl.commitCalled);
    }

    @Test
    void testSkillUpgradeRejectedByAdmin() {
        activitiesImpl.securityResult = "PASSED";

        io.temporal.client.WorkflowClient.start(workflow::proposeSkillUpgrade, "session-2", "API_INTEGRATION", 3);

        // Signal human rejection
        workflow.adminReviewDecision(false);

        String result = io.temporal.client.WorkflowStub.fromTyped(workflow).getResult(String.class);
        assertEquals("SKILL_UPGRADE_REJECTED_BY_ADMIN", result);

        // Verify commit was NEVER called
        assertFalse(activitiesImpl.commitCalled);
    }
}
