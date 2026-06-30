package com.xbank.opf.patterns.harness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ModelAgnosticHarnessTest {

    private AiModelClient mockClient;
    private MemoryBank mockMemoryBank;
    private ModelAgnosticHarness harness;

    @BeforeEach
    void setUp() {
        mockClient = mock(AiModelClient.class);
        mockMemoryBank = mock(MemoryBank.class);
        harness = new ModelAgnosticHarness(mockClient, mockMemoryBank);
    }

    @Test
    void executeWithMemory_savesBothUserAndAiMessage() {
        String sessionId = "sess-123";
        String systemPrompt = "You are a helpful assistant.";
        String userPrompt = "Hello!";
        
        AiMessage fakeHistoryMessage = new AiMessage("user", "old history");
        when(mockMemoryBank.getHistory(sessionId)).thenReturn(List.of(fakeHistoryMessage));
        
        AiMessage mockAiResponse = new AiMessage("assistant", "Hi there!");
        when(mockClient.executeInference(eq(systemPrompt), anyList(), eq(userPrompt)))
            .thenReturn(mockAiResponse);

        String result = harness.executeWithMemory(sessionId, systemPrompt, userPrompt);

        assertEquals("Hi there!", result);

        ArgumentCaptor<AiMessage> messageCaptor = ArgumentCaptor.forClass(AiMessage.class);
        verify(mockMemoryBank, times(2)).saveMessage(eq(sessionId), messageCaptor.capture());

        List<AiMessage> savedMessages = messageCaptor.getAllValues();
        assertEquals("user", savedMessages.get(0).getRole());
        assertEquals("Hello!", savedMessages.get(0).getContent());
        
        assertEquals("assistant", savedMessages.get(1).getRole());
        assertEquals("Hi there!", savedMessages.get(1).getContent());
    }
}
