package com.xbank.opf.patterns.fte.economics;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.FteOnboardingService;
import com.xbank.opf.patterns.fte.config.FteProperties;
import com.xbank.opf.patterns.harness.AgentFteData;
import com.xbank.opf.patterns.harness.AiMessage;
import com.xbank.opf.patterns.harness.MemoryBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FteCostOptimizerTest {

    private TestMemoryBank memoryBank;
    private FteCostOptimizer optimizer;

    static class TestMemoryBank implements MemoryBank {
        public Map<String, AgentFteData> storage = new HashMap<>();

        @Override
        public List<AiMessage> getHistory(String sessionId) { return List.of(); }

        @Override
        public void saveMessage(String sessionId, AiMessage message) {}

        @Override
        public AgentFteData getFteData(String sessionId) { return storage.get(sessionId); }

        @Override
        public void updateFteData(String sessionId, AgentFteData data) {
            storage.put(sessionId, data);
        }
    }

    @BeforeEach
    void setUp() {
        memoryBank = new TestMemoryBank();
        
        FteProperties properties = new FteProperties();
        properties.getEconomics().setCostLow(new BigDecimal("0.05"));
        properties.getEconomics().setCostMedium(new BigDecimal("0.50"));
        properties.getEconomics().setCostHigh(new BigDecimal("2.00"));
        
        optimizer = new FteCostOptimizer(memoryBank, properties);
    }

    @Test
    void testAuthorizeAndConsumeSuccess() {
        String sessionId = FteOnboardingService.getSessionIdForRole(AgentFteRole.TOPOLOGY_SYNTHESIS);
        AgentFteData data = new AgentFteData(AgentFteRole.TOPOLOGY_SYNTHESIS, "Test Agent");
        data.getEconomicsProfile().setCurrentBudget(new BigDecimal("10.00"));
        memoryBank.updateFteData(sessionId, data);

        // A medium prompt length (between 100 and 1000 chars)
        String mediumPrompt = "A".repeat(500);
        
        optimizer.authorizeAndConsume(AgentFteRole.TOPOLOGY_SYNTHESIS, mediumPrompt);

        AgentFteData updatedData = memoryBank.getFteData(sessionId);
        // Medium complexity cost is 0.50
        assertEquals(new BigDecimal("9.50"), updatedData.getEconomicsProfile().getCurrentBudget());
        assertEquals(new BigDecimal("0.50"), updatedData.getEconomicsProfile().getLifetimeConsumption());
    }

    @Test
    void testAuthorizeAndConsumeThrowsInsufficientBudgetException() {
        String sessionId = FteOnboardingService.getSessionIdForRole(AgentFteRole.COMPLIANCE_VALIDATION);
        AgentFteData data = new AgentFteData(AgentFteRole.COMPLIANCE_VALIDATION, "Test Agent");
        // Set budget lower than HIGH complexity cost (2.00)
        data.getEconomicsProfile().setCurrentBudget(new BigDecimal("1.00"));
        memoryBank.updateFteData(sessionId, data);

        // A high prompt length (>= 1000 chars)
        String highPrompt = "B".repeat(1500);

        InsufficientFteBudgetException exception = assertThrows(
                InsufficientFteBudgetException.class,
                () -> optimizer.authorizeAndConsume(AgentFteRole.COMPLIANCE_VALIDATION, highPrompt)
        );

        assertEquals(AgentFteRole.COMPLIANCE_VALIDATION, exception.getRole());
        assertEquals(new BigDecimal("1.00"), exception.getCurrentBudget());
        assertEquals(new BigDecimal("2.00"), exception.getRequiredCost());
        assertTrue(exception.getMessage().contains("insufficient budget"));
    }

    @Test
    void testAuthorizeAndConsumeThrowsIllegalStateExceptionIfNoProfile() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> optimizer.authorizeAndConsume(AgentFteRole.AUTONOMOUS_INGESTION, "Short prompt")
        );

        assertTrue(exception.getMessage().contains("Agent FTE profile not found"));
    }
}
