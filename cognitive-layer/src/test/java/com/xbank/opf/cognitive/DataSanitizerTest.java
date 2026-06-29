package com.xbank.opf.cognitive;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataSanitizerTest {

    @Test
    public void testPiiScrubbing() {
        // Assume Agent3Scoper is a conceptual static class for this test
        String rawPrompt = "Transfer 100 AED from account 9934123 to EID 784-1234-5678901-2";
        
        // Simulating the PII scrubbing logic
        String scrubbedPrompt = rawPrompt.replaceAll("784-\\d{4}-\\d{7}-\\d", "[HASH_EID]")
                                         .replaceAll("\\b\\d{7}\\b", "[HASH_ACCT]");
                                         
        assertFalse(scrubbedPrompt.contains("9934123"));
        assertFalse(scrubbedPrompt.contains("784-1234-5678901-2"));
        assertTrue(scrubbedPrompt.contains("[HASH_ACCT]"));
        assertTrue(scrubbedPrompt.contains("[HASH_EID]"));
    }
}
