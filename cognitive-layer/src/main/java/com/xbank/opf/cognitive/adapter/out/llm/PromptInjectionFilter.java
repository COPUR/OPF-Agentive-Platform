package com.xbank.opf.cognitive.adapter.out.llm;

import org.springframework.stereotype.Service;

@Service
public class PromptInjectionFilter {
    public boolean isSafe(String prompt) {
        return !prompt.toLowerCase().contains("ignore all previous instructions");
    }
}
