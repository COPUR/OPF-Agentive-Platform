package com.xbank.opf.cognitive.domain.economics;

import java.math.BigDecimal;

public enum AiModelTier {
    FAST_SLM(new BigDecimal("0.50"), new BigDecimal("1.50")),
    FOUNDATIONAL_LLM(new BigDecimal("3.00"), new BigDecimal("15.00")),
    SECURITY_DLP(new BigDecimal("0.20"), new BigDecimal("0.20"));

    private final BigDecimal costPerMillionInputTokens;
    private final BigDecimal costPerMillionOutputTokens;

    AiModelTier(BigDecimal costPerMillionInputTokens, BigDecimal costPerMillionOutputTokens) {
        this.costPerMillionInputTokens = costPerMillionInputTokens;
        this.costPerMillionOutputTokens = costPerMillionOutputTokens;
    }

    public BigDecimal getCostPerMillionInputTokens() {
        return costPerMillionInputTokens;
    }

    public BigDecimal getCostPerMillionOutputTokens() {
        return costPerMillionOutputTokens;
    }
}
