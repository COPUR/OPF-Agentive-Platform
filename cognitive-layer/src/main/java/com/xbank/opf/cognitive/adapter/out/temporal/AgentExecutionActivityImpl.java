package com.xbank.opf.cognitive.adapter.out.temporal;

import com.xbank.opf.cognitive.adapter.out.llm.LlmClient;
import com.xbank.opf.cognitive.application.HarnessOrchestrator;
import com.xbank.opf.cognitive.domain.RequirementDocument;
import com.xbank.opf.cognitive.domain.StructuredTask;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentExecutionActivityImpl implements AgentExecutionActivity {

    private final HarnessOrchestrator harnessOrchestrator;
    private final LlmClient llmClient;

    public AgentExecutionActivityImpl(HarnessOrchestrator harnessOrchestrator, LlmClient llmClient) {
        this.harnessOrchestrator = harnessOrchestrator;
        this.llmClient = llmClient;
    }

    @Override
    public List<StructuredTask> parseRequirements(RequirementDocument document) {
        // Delegates to the orchestrator to convert PRD -> JSON/Tasks
        return harnessOrchestrator.orchestrateRequirements(document);
    }

    @Override
    public String executeIsolatedTask(StructuredTask task) {
        // Represents "Isolated State Management" - executes one task with a clean slate
        return llmClient.executeTask(task);
    }
}
