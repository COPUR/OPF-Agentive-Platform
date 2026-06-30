package com.xbank.opf.cognitive.application.economics;

import com.xbank.opf.cognitive.domain.economics.AiModelTier;
import com.xbank.opf.cognitive.domain.economics.TokenUsage;
import com.xbank.opf.cognitive.domain.StructuredTask;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AiEconomicsCalculator {

    private static final BigDecimal ONE_MILLION = new BigDecimal("1000000");

    /**
     * Calculates the exact monetary cost of a single inference given token counts.
     * @param usage The TokenUsage details.
     * @return Cost in standard unit (e.g. USD).
     */
    public BigDecimal calculateCost(TokenUsage usage) {
        BigDecimal inputCost = usage.tier().getCostPerMillionInputTokens()
                .multiply(BigDecimal.valueOf(usage.inputTokens()))
                .divide(ONE_MILLION, 6, RoundingMode.HALF_UP);

        BigDecimal outputCost = usage.tier().getCostPerMillionOutputTokens()
                .multiply(BigDecimal.valueOf(usage.outputTokens()))
                .divide(ONE_MILLION, 6, RoundingMode.HALF_UP);

        return inputCost.add(outputCost);
    }

    /**
     * Estimates the budget required for a list of structured tasks, assuming an average token count.
     * @param tasks The tasks to execute.
     * @param assumedTier The assumed model tier.
     * @param avgInputTokens Assumed input tokens per task.
     * @param avgOutputTokens Assumed output tokens per task.
     * @return Estimated total cost.
     */
    public BigDecimal planBudget(List<StructuredTask> tasks, AiModelTier assumedTier, long avgInputTokens, long avgOutputTokens) {
        if (tasks == null || tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long totalInputTokens = avgInputTokens * tasks.size();
        long totalOutputTokens = avgOutputTokens * tasks.size();

        return calculateCost(new TokenUsage(assumedTier, totalInputTokens, totalOutputTokens));
    }
}
