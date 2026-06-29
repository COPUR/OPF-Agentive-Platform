package com.xbank.opf.cognitive.domain;

/**
 * Represents the unstructured or semi-structured Product Requirements Document (PRD)
 * or intent context provided to the system.
 */
public record RequirementDocument(
        String requirementId,
        String rawContent,
        String source
) {
}
