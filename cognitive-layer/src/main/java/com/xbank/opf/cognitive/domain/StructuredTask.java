package com.xbank.opf.cognitive.domain;

import java.util.Map;

/**
 * Represents a discrete, atomic task parsed from the RequirementDocument.
 * Contains the specific prompt, constraints, and isolated context required for a single agent execution step.
 */
public record StructuredTask(
        String taskId,
        String description,
        String isolatedPrompt,
        Map<String, Object> contextParameters,
        int stepOrder
) {
}
