package com.xbank.opf.patterns.fte.economics;

public enum FteTaskComplexity {
    LOW,     // Simple parsing, single lookups
    MEDIUM,  // Synthesis, topology generation
    HIGH;    // Deep context vector checks, extensive compliance logic

    /**
     * Infers the complexity of a task based on the length/depth of the prompt.
     * This is a simple heuristic mapping.
     */
    public static FteTaskComplexity inferFromPrompt(String prompt) {
        if (prompt == null || prompt.length() < 100) {
            return LOW;
        } else if (prompt.length() < 1000) {
            return MEDIUM;
        } else {
            return HIGH;
        }
    }
}
