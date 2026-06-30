package com.xbank.opf.cognitive.domain.economics;

public record TokenUsage(AiModelTier tier, long inputTokens, long outputTokens) {
    public TokenUsage {
        if (inputTokens < 0 || outputTokens < 0) {
            throw new IllegalArgumentException("Token counts cannot be negative");
        }
        if (tier == null) {
            throw new IllegalArgumentException("AiModelTier cannot be null");
        }
    }
}
