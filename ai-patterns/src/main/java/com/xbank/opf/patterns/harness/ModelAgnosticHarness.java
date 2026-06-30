package com.xbank.opf.patterns.harness;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelAgnosticHarness {

    private final AiModelClient modelClient;
    private final MemoryBank memoryBank;

    public ModelAgnosticHarness(AiModelClient modelClient, MemoryBank memoryBank) {
        this.modelClient = modelClient;
        this.memoryBank = memoryBank;
    }

    /**
     * Executes a prompt with memory context, persisting both the user prompt and the AI's response.
     * @param sessionId Session ID to scope memory.
     * @param systemPrompt System instructions.
     * @param prompt The new user input.
     * @return The AI's generated response content.
     */
    public String executeWithMemory(String sessionId, String systemPrompt, String prompt) {
        // 1. Retrieve history
        List<AiMessage> history = memoryBank.getHistory(sessionId);

        // 2. Execute inference via the agnostic client
        AiMessage aiResponse = modelClient.executeInference(systemPrompt, history, prompt);

        // 3. Store the user prompt and AI response to the Memory Bank
        memoryBank.saveMessage(sessionId, new AiMessage("user", prompt));
        memoryBank.saveMessage(sessionId, aiResponse);

        return aiResponse.getContent();
    }
}
