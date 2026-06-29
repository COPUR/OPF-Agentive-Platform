package com.xbank.opf.cognitive;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.xbank.opf.cognitive.adapter.out.llm.Agent3Scoper;

public class DataSanitizerTest {

    @Test
    public void testPiiScrubbing() {
        Agent3Scoper scoper = new Agent3Scoper();
        String rawPrompt = "Transfer 100 AED from account 9934123 to EID 784-1234-5678901-2";
        
        String scrubbedPrompt = scoper.scrubPii(rawPrompt);
                                         
        assertFalse(scrubbedPrompt.contains("9934123"));
        assertFalse(scrubbedPrompt.contains("784-1234-5678901-2"));
        assertTrue(scrubbedPrompt.contains("[HASH_ACCT]"));
        assertTrue(scrubbedPrompt.contains("[HASH_EID]"));
    }
}
