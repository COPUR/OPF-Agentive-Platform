package com.xbank.opf.cognitive.adapter.out.llm;

import org.springframework.stereotype.Service;

@Service
public class Agent3Scoper {
    public String scrubPii(String rawPrompt) {
        return rawPrompt.replaceAll("784-\\d{4}-\\d{7}-\\d", "[HASH_EID]")
                        .replaceAll("\\b\\d{7}\\b", "[HASH_ACCT]");
    }
}
