package com.xbank.opf.cognitive.application.economics;

import com.xbank.opf.cognitive.domain.economics.AiModelTier;
import com.xbank.opf.cognitive.domain.economics.TokenUsage;
import com.xbank.opf.cognitive.domain.StructuredTask;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AiEconomicsCalculatorTest {

    private final AiEconomicsCalculator calculator = new AiEconomicsCalculator();

    @Test
    void shouldCalculateCostForFastSlm() {
        TokenUsage usage = new TokenUsage(AiModelTier.FAST_SLM, 100_000, 50_000);
        // Cost: (0.50 * 0.1) + (1.50 * 0.05) = 0.05 + 0.075 = 0.125
        BigDecimal cost = calculator.calculateCost(usage);
        assertEquals(0, cost.compareTo(new BigDecimal("0.125000")));
    }

    @Test
    void shouldCalculateCostForFoundationalLlm() {
        TokenUsage usage = new TokenUsage(AiModelTier.FOUNDATIONAL_LLM, 1_000_000, 500_000);
        // Cost: (3.00 * 1) + (15.00 * 0.5) = 3.00 + 7.50 = 10.50
        BigDecimal cost = calculator.calculateCost(usage);
        assertEquals(0, cost.compareTo(new BigDecimal("10.500000")));
    }

    @Test
    void shouldPlanBudgetForOrchestratedTasks() {
        StructuredTask task1 = new StructuredTask(UUID.randomUUID().toString(), "Task 1", "desc", java.util.Map.of(), 1);
        StructuredTask task2 = new StructuredTask(UUID.randomUUID().toString(), "Task 2", "desc", java.util.Map.of(), 2);

        List<StructuredTask> tasks = List.of(task1, task2);
        
        // 2 tasks * 500,000 input = 1,000,000 input tokens
        // 2 tasks * 200,000 output = 400,000 output tokens
        // Tier: FOUNDATIONAL_LLM (3.00/1M in, 15.00/1M out)
        // Cost: (3.00 * 1) + (15.00 * 0.4) = 3.00 + 6.00 = 9.00

        BigDecimal budget = calculator.planBudget(tasks, AiModelTier.FOUNDATIONAL_LLM, 500_000, 200_000);
        assertEquals(0, budget.compareTo(new BigDecimal("9.000000")));
    }

    @Test
    void shouldRejectNegativeTokens() {
        assertThrows(IllegalArgumentException.class, () -> new TokenUsage(AiModelTier.FAST_SLM, -10, 50));
    }
}
