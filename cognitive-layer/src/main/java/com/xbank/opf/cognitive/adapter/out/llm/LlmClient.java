package com.xbank.opf.cognitive.adapter.out.llm;

import com.xbank.opf.cognitive.domain.StructuredTask;
import java.util.List;

public interface LlmClient {
    /**
     * Translates an unstructured requirement document into a list of structured tasks.
     * @param rawPrdContent The raw product requirements document text.
     * @return A list of atomic, structured tasks.
     */
    List<StructuredTask> parseRequirementsToTasks(String rawPrdContent);

    /**
     * Executes a single structured task in an isolated context.
     * @param task The task to execute.
     * @return The execution result.
     */
    String executeTask(StructuredTask task);
}
