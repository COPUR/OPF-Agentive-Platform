package com.xbank.opf.patterns.fte.economics;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.FteOnboardingService;
import com.xbank.opf.patterns.fte.config.FteProperties;
import com.xbank.opf.patterns.harness.AgentFteData;
import com.xbank.opf.patterns.harness.MemoryBank;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FteCostOptimizer {

    private final MemoryBank memoryBank;
    private final FteProperties properties;

    public FteCostOptimizer(MemoryBank memoryBank, FteProperties properties) {
        this.memoryBank = memoryBank;
        this.properties = properties;
    }

    /**
     * Authorizes and consumes the estimated AI credits required to perform the task.
     * Throws an exception if the FTE does not have enough budget.
     *
     * @param role The Agent-FTE performing the task.
     * @param prompt The task prompt.
     */
    public void authorizeAndConsume(AgentFteRole role, String prompt) {
        String sessionId = FteOnboardingService.getSessionIdForRole(role);
        AgentFteData fteData = memoryBank.getFteData(sessionId);

        if (fteData == null) {
            throw new IllegalStateException("Agent FTE profile not found in memory bank for role: " + role);
        }

        FteEconomicsProfile economy = fteData.getEconomicsProfile();
        FteTaskComplexity inferredComplexity = FteTaskComplexity.inferFromPrompt(prompt);
        BigDecimal requiredCost = getCostForComplexity(inferredComplexity);

        if (economy.getCurrentBudget().compareTo(requiredCost) < 0) {
            throw new InsufficientFteBudgetException(role, economy.getCurrentBudget(), requiredCost);
        }

        // Consume the budget and persist
        economy.consume(requiredCost);
        memoryBank.updateFteData(sessionId, fteData);
        
        System.out.println("Cost Optimizer: Authorized " + requiredCost + " credits for " + role + " (" + inferredComplexity + " complexity). Remaining budget: " + economy.getCurrentBudget());
    }

    private BigDecimal getCostForComplexity(FteTaskComplexity complexity) {
        return switch (complexity) {
            case LOW -> properties.getEconomics().getCostLow();
            case MEDIUM -> properties.getEconomics().getCostMedium();
            case HIGH -> properties.getEconomics().getCostHigh();
        };
    }
}
