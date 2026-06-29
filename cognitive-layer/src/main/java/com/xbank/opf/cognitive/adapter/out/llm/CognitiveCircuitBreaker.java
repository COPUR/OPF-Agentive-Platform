package com.xbank.opf.cognitive.adapter.out.llm;

import org.springframework.stereotype.Service;

@Service
public class CognitiveCircuitBreaker {
    public String executeWithFallback(boolean llmIsDown) {
        if (llmIsDown) {
            return "DETERMINISTIC_RULES_ENGINE";
        }
        return "LLM_INFERENCE";
    }
}
