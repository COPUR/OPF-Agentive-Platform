package com.xbank.opf.cognitive.adapter.out.temporal;

import com.xbank.opf.cognitive.domain.RequirementDocument;
import com.xbank.opf.cognitive.domain.StructuredTask;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OpenFinanceWorkflowImplTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private WorkflowClient workflowClient;

    @BeforeEach
    public void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker("AgentiveTaskQueue");
        worker.registerWorkflowImplementationTypes(OpenFinanceWorkflowImpl.class);
        workflowClient = testEnv.getWorkflowClient();
    }

    @AfterEach
    public void tearDown() {
        testEnv.close();
    }

    @Test
    public void testExecuteAgenticWorkflow() {
        StructuredTask mockTask1 = new StructuredTask("task-1", "Step 1", "Prompt 1", Map.of(), 1);
        StructuredTask mockTask2 = new StructuredTask("task-2", "Step 2", "Prompt 2", Map.of(), 2);

        // Use anonymous inner class instead of mock to satisfy Temporal's annotation checker
        AgentExecutionActivity mockActivity = new AgentExecutionActivity() {
            @Override
            public List<StructuredTask> parseRequirements(RequirementDocument document) {
                return List.of(mockTask1, mockTask2);
            }

            @Override
            public String executeIsolatedTask(StructuredTask task) {
                if (task.taskId().equals("task-1")) return "Result 1";
                if (task.taskId().equals("task-2")) return "Result 2";
                return "Unknown";
            }
        };

        worker.registerActivitiesImplementations(mockActivity);
        testEnv.start();

        OpenFinanceWorkflow workflow = workflowClient.newWorkflowStub(
                OpenFinanceWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue("AgentiveTaskQueue").build()
        );

        RequirementDocument doc = new RequirementDocument("req-1", "Make a new portal", "jira");
        List<String> results = workflow.executeAgenticWorkflow(doc);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Result 1", results.get(0));
        assertEquals("Result 2", results.get(1));
    }
}
