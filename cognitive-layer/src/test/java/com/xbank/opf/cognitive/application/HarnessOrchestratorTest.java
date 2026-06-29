package com.xbank.opf.cognitive.application;

import com.xbank.opf.cognitive.adapter.out.llm.LlmClient;
import com.xbank.opf.cognitive.domain.RequirementDocument;
import com.xbank.opf.cognitive.domain.StructuredTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class HarnessOrchestratorTest {

    private LlmClient mockLlmClient;
    private HarnessOrchestrator harnessOrchestrator;

    @BeforeEach
    public void setUp() {
        mockLlmClient = Mockito.mock(LlmClient.class);
        harnessOrchestrator = new HarnessOrchestrator(mockLlmClient);
    }

    @Test
    public void testOrchestrateRequirements() {
        RequirementDocument req = new RequirementDocument("1", "Create a login page.", "jira");
        StructuredTask mockTask = new StructuredTask("t1", "desc", "prompt", Map.of(), 1);

        when(mockLlmClient.parseRequirementsToTasks(anyString())).thenReturn(List.of(mockTask));

        List<StructuredTask> tasks = harnessOrchestrator.orchestrateRequirements(req);

        assertEquals(1, tasks.size());
        assertEquals("t1", tasks.get(0).taskId());
    }
}
