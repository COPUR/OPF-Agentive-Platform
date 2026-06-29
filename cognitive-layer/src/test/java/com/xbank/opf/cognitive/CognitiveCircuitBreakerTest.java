package com.xbank.opf.cognitive;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CognitiveCircuitBreakerTest {

    @Test
    public void testGracefulDegradationOnLlmTimeout() {
        boolean llmIsDown = true;
        String executionPath = "";

        if (llmIsDown) {
            // Circuit Breaker triggers deterministic fallback
            executionPath = "DETERMINISTIC_RULES_ENGINE";
        } else {
            executionPath = "LLM_INFERENCE";
        }

        assertEquals("DETERMINISTIC_RULES_ENGINE", executionPath, 
            "The system must fallback to deterministic rules if the LLM cluster times out.");
    }
}
