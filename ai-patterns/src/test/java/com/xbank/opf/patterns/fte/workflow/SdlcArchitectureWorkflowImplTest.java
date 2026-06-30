package com.xbank.opf.patterns.fte.workflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SdlcArchitectureWorkflowImplTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private WorkflowClient workflowClient;

    static class TestSdlcActivities implements SdlcArchitectureActivities {
        @Override
        public String executeIngestion(String jiraPrdInput) {
            return "Ingested Context for: " + jiraPrdInput;
        }

        @Override
        public String executeTopology(String ingestedContext) {
            return "Generated LLD for " + ingestedContext;
        }

        @Override
        public String executeCompliance(String lldContext) {
            return "Validated LLD! Zero violations.";
        }
    }

    @BeforeEach
    void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker("SDLC_TASK_QUEUE");
        worker.registerWorkflowImplementationTypes(SdlcArchitectureWorkflowImpl.class);

        // Register custom test activities
        worker.registerActivitiesImplementations(new TestSdlcActivities());

        testEnv.start();
        workflowClient = testEnv.getWorkflowClient();
    }

    @AfterEach
    void tearDown() {
        testEnv.close();
    }

    @Test
    void testExecuteEndToEndArchitecture() {
        SdlcArchitectureWorkflow workflow = workflowClient.newWorkflowStub(
                SdlcArchitectureWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue("SDLC_TASK_QUEUE").build()
        );

        String result = workflow.executeEndToEndArchitecture("New Credit Card API Requirements");

        assertEquals("SDLC E2E Complete. Compliance Result: Validated LLD! Zero violations.", result);
    }
}
