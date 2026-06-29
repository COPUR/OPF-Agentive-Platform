package com.xbank.opf.cognitive;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.xbank.opf.cognitive.adapter.out.llm.CognitiveCircuitBreaker;

public class CognitiveCircuitBreakerTest {

    @Test
    public void testGracefulDegradationOnLlmTimeout() {
        CognitiveCircuitBreaker breaker = new CognitiveCircuitBreaker();
        boolean llmIsDown = true;
        String executionPath = breaker.executeWithFallback(llmIsDown);

        assertEquals("DETERMINISTIC_RULES_ENGINE", executionPath, 
            "The system must fallback to deterministic rules if the LLM cluster times out.");
            
        String executionPathUp = breaker.executeWithFallback(false);
        assertEquals("LLM_INFERENCE", executionPathUp);
    }
}
