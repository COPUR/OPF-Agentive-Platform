package com.xbank.opf.sdk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.xbank.opf.sdk.security.ConsentManager;

public class ConsentManagerTest {

    @Test
    public void testScaUrlGeneration() {
        ConsentManager manager = new ConsentManager();
        String url = manager.initiateConsentFlow("DOMESTIC_PAYMENT");
        
        assertNotNull(url);
        assertTrue(url.startsWith("https://auth.opf.xbank.com/consent"));
        assertTrue(url.contains("scope=DOMESTIC_PAYMENT"));
    }

    @Test
    public void testConsentPolling() {
        ConsentManager manager = new ConsentManager();
        boolean status = manager.checkConsentStatus("intent-12345");
        
        // Mock implementation always returns true for now
        assertTrue(status);
    }
}
