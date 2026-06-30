package com.xbank.opf.patterns.harness;

import java.util.List;

public interface MemoryBank {
    /**
     * Appends a new message to the session's memory bank.
     * @param sessionId The unique ID for the agent session.
     * @param message The message to append.
     */
    void saveMessage(String sessionId, AiMessage message);

    /**
     * Retrieves the entire interaction history for a given session.
     * @param sessionId The unique ID for the agent session.
     * @return A list of chronological AiMessages.
     */
    List<AiMessage> getHistory(String sessionId);

    /**
     * Retrieves the current Agent-FTE profile data.
     * @param sessionId The unique ID for the agent session.
     * @return The AgentFteData.
     */
    AgentFteData getFteData(String sessionId);

    /**
     * Updates the Agent-FTE profile data.
     * @param sessionId The unique ID for the agent session.
     * @param updatedData The updated AgentFteData.
     */
    void updateFteData(String sessionId, AgentFteData updatedData);
}
