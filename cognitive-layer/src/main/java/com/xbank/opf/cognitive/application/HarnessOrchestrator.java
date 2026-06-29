package com.xbank.opf.cognitive.application;

import com.xbank.opf.cognitive.adapter.out.llm.LlmClient;
import com.xbank.opf.cognitive.domain.RequirementDocument;
import com.xbank.opf.cognitive.domain.StructuredTask;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HarnessOrchestrator {

    private final LlmClient llmClient;

    public HarnessOrchestrator(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    /**
     * Orchestrates the parsing of a requirement document into structured tasks,
     * fulfilling the "Structured Requirements" step of the Ralph architecture.
     *
     * @param requirementDocument The unstructured requirements.
     * @return A list of discrete tasks to be executed iteratively.
     */
    public List<StructuredTask> orchestrateRequirements(RequirementDocument requirementDocument) {
        // Here we could add pre-processing, validation, or persistence logic
        return llmClient.parseRequirementsToTasks(requirementDocument.rawContent());
    }
}
