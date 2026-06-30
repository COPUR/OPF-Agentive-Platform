package com.xbank.opf.patterns.fte.integration;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.FteHarnessCoordinator;
import com.xbank.opf.patterns.fte.integration.adapter.in.AgentIngestionKafkaListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentIngestionKafkaListenerTest {

    private FakeHarnessCoordinator fakeHarnessCoordinator;
    private AgentIngestionKafkaListener kafkaListener;

    @BeforeEach
    void setUp() {
        fakeHarnessCoordinator = new FakeHarnessCoordinator();
        kafkaListener = new AgentIngestionKafkaListener(fakeHarnessCoordinator);
    }

    @Test
    void testConsumeOpenFinanceEvent_Success() {
        String mockEvent = "{\"type\":\"ConsentCreated\", \"tppId\":\"tpp_123\"}";
        
        kafkaListener.consumeOpenFinanceEvent(mockEvent);

        assertEquals(1, fakeHarnessCoordinator.dispatchCount);
        assertEquals(AgentFteRole.AUTONOMOUS_INGESTION, fakeHarnessCoordinator.lastRole);
        assertTrue(fakeHarnessCoordinator.lastPrompt.contains(mockEvent));
    }

    @Test
    void testConsumeOpenFinanceEvent_ExceptionHandled() {
        String mockEvent = "{\"type\":\"ConsentCreated\"}";
        fakeHarnessCoordinator.shouldThrow = true;

        // Should not throw exception out of the listener, just log it
        kafkaListener.consumeOpenFinanceEvent(mockEvent);

        assertEquals(1, fakeHarnessCoordinator.dispatchCount);
    }

    // Manual Fake to replace Mockito
    private static class FakeHarnessCoordinator extends FteHarnessCoordinator {
        int dispatchCount = 0;
        AgentFteRole lastRole;
        String lastPrompt;
        boolean shouldThrow = false;

        public FakeHarnessCoordinator() {
            super(null, null, null, null, null);
        }

        @Override
        public String dispatchTask(AgentFteRole role, String taskPrompt) {
            dispatchCount++;
            lastRole = role;
            lastPrompt = taskPrompt;
            if (shouldThrow) {
                throw new RuntimeException("API failure");
            }
            return "Agent Response: Consented successfully";
        }
    }
}
