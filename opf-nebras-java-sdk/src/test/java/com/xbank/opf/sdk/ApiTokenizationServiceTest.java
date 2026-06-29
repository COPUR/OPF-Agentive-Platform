package com.xbank.opf.sdk;

import com.xbank.opf.sdk.security.ApiTokenizationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApiTokenizationServiceTest {

    @Test
    public void testTokenExchange() {
        ApiTokenizationService service = new ApiTokenizationService();
        String token = service.exchangeConsentForToken("consent-123");
        assertNotNull(token);
        assertTrue(token.startsWith("opf_token_"));
    }

    @Test
    public void testTokenRefresh() {
        ApiTokenizationService service = new ApiTokenizationService();
        String token = service.refreshToken("old-token");
        assertNotNull(token);
        assertTrue(token.startsWith("opf_token_"));
    }
}
