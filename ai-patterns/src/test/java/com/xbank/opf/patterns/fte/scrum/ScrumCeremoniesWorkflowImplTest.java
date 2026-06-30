package com.xbank.opf.patterns.fte.scrum;

import com.xbank.opf.patterns.fte.scrum.workflow.ScrumCeremoniesActivities;
import com.xbank.opf.patterns.fte.scrum.workflow.ScrumCeremoniesWorkflow;
import com.xbank.opf.patterns.fte.scrum.workflow.ScrumCeremoniesWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScrumCeremoniesWorkflowImplTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private ScrumCeremoniesWorkflow workflow;

    public static class TestScrumActivities implements ScrumCeremoniesActivities {
        @Override
        public String runSprintPlanning(String backlog) {
            return "Planning OK";
        }

        @Override
        public String runDailyStandup(String sprintGoal) {
            return "Standup OK";
        }

        @Override
        public String runSprintReview(String deliverable) {
            return "Review OK";
        }

        @Override
        public String runSprintRetrospective(String performanceMetrics) {
            return "Retro OK";
        }

        @Override
        public String generateScrumReport(String planning, String standup, String review, String retro) {
            return "Final Report OK";
        }
    }

    @BeforeEach
    void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker("FTE_SCRUM_TASK_QUEUE");

        worker.registerWorkflowImplementationTypes(ScrumCeremoniesWorkflowImpl.class);
        worker.registerActivitiesImplementations(new TestScrumActivities());

        testEnv.start();

        workflow = testEnv.getWorkflowClient().newWorkflowStub(
                ScrumCeremoniesWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue("FTE_SCRUM_TASK_QUEUE").build()
        );
    }

    @AfterEach
    void tearDown() {
        testEnv.close();
    }

    @Test
    void testScrumCycleApproved() {
        // Start async
        WorkflowClient.start(workflow::executeScrumCycle, "Test Sprint Context");

        // Send approval signal
        workflow.approveScrumReport(true);

        // Wait and get result
        String result = WorkflowStub.fromTyped(workflow).getResult(String.class);

        assertEquals("SCRUM_CYCLE_APPROVED_BY_HITL", result);
    }

    @Test
    void testScrumCycleRejected() {
        WorkflowClient.start(workflow::executeScrumCycle, "Test Sprint Context");

        workflow.approveScrumReport(false);

        String result = WorkflowStub.fromTyped(workflow).getResult(String.class);

        assertEquals("SCRUM_CYCLE_REJECTED_BY_HITL", result);
    }
}
