package com.xbank.opf.patterns.harness;

import java.util.List;

public interface AiModelClient {
    /**
     * Executes an inference call using the provided context and new prompt.
     * @param systemPrompt The overarching instructions for the AI.
     * @param history Prior conversational context.
     * @param newPrompt The new user instruction.
     * @return The AI's response message.
     */
    AiMessage executeInference(String systemPrompt, List<AiMessage> history, String newPrompt);
}
